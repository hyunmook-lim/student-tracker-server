#!/bin/bash

# AWS ECS 배포 스크립트
echo "🚀 AWS ECS 배포를 시작합니다..."

# 설정
AWS_REGION="ap-northeast-2"
ECR_REPOSITORY="studenttracker"
CLUSTER_NAME="studenttracker-cluster"
SERVICE_NAME="studenttracker-service"
TASK_DEFINITION="studenttracker-task"

# 1. ECR 로그인
echo "🔐 ECR에 로그인합니다..."
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $(aws sts get-caller-identity --query Account --output text).dkr.ecr.$AWS_REGION.amazonaws.com

# 2. ECR 리포지토리 생성 (없는 경우)
echo "📦 ECR 리포지토리를 확인/생성합니다..."
aws ecr describe-repositories --repository-names $ECR_REPOSITORY --region $AWS_REGION 2>/dev/null || \
aws ecr create-repository --repository-name $ECR_REPOSITORY --region $AWS_REGION

# 3. 이미지 빌드 및 푸시
echo "🏗️ Docker 이미지를 빌드하고 푸시합니다..."
ECR_URI=$(aws sts get-caller-identity --query Account --output text).dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY

docker build -t $ECR_REPOSITORY .
docker tag $ECR_REPOSITORY:latest $ECR_URI:latest
docker push $ECR_URI:latest

# 4. ECS 클러스터 생성 (없는 경우)
echo "🔧 ECS 클러스터를 확인/생성합니다..."
aws ecs describe-clusters --clusters $CLUSTER_NAME --region $AWS_REGION 2>/dev/null || \
aws ecs create-cluster --cluster-name $CLUSTER_NAME --region $AWS_REGION

# 5. 태스크 정의 등록
echo "📋 태스크 정의를 등록합니다..."
cat > task-definition.json << EOF
{
  "family": "$TASK_DEFINITION",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "256",
  "memory": "512",
  "executionRoleArn": "arn:aws:iam::$(aws sts get-caller-identity --query Account --output text):role/ecsTaskExecutionRole",
  "containerDefinitions": [
    {
      "name": "studenttracker",
      "image": "$ECR_URI:latest",
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/$TASK_DEFINITION",
          "awslogs-region": "$AWS_REGION",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]
}
EOF

aws ecs register-task-definition --cli-input-json file://task-definition.json --region $AWS_REGION

# 6. 서비스 생성 또는 업데이트
echo "🔄 서비스를 생성/업데이트합니다..."
SERVICE_EXISTS=$(aws ecs describe-services --cluster $CLUSTER_NAME --services $SERVICE_NAME --region $AWS_REGION --query 'services[0].status' --output text 2>/dev/null)

if [ "$SERVICE_EXISTS" = "ACTIVE" ]; then
    aws ecs update-service --cluster $CLUSTER_NAME --service $SERVICE_NAME --task-definition $TASK_DEFINITION --region $AWS_REGION
else
    aws ecs create-service \
        --cluster $CLUSTER_NAME \
        --service-name $SERVICE_NAME \
        --task-definition $TASK_DEFINITION \
        --desired-count 1 \
        --launch-type FARGATE \
        --network-configuration "awsvpcConfiguration={subnets=[subnet-xxxxxxxxx],securityGroups=[sg-xxxxxxxxx],assignPublicIp=ENABLED}" \
        --region $AWS_REGION
fi

echo "✅ ECS 배포가 완료되었습니다!"
echo "📊 서비스 상태 확인: aws ecs describe-services --cluster $CLUSTER_NAME --services $SERVICE_NAME --region $AWS_REGION"
