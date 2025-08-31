#!/bin/bash

# AWS Elastic Beanstalk 배포 스크립트
echo "🚀 AWS Elastic Beanstalk 배포를 시작합니다..."

# 1. 애플리케이션 빌드
echo "📦 애플리케이션을 빌드합니다..."
./gradlew clean build -x test

# 2. EB CLI 설치 확인
if ! command -v eb &> /dev/null; then
    echo "❌ EB CLI가 설치되지 않았습니다."
    echo "다음 명령어로 설치하세요:"
    echo "pip install awsebcli"
    exit 1
fi

# 3. EB 초기화 (처음 실행 시)
if [ ! -f ".elasticbeanstalk/config.yml" ]; then
    echo "🔧 EB 환경을 초기화합니다..."
    eb init
fi

# 4. 환경 생성 또는 업데이트
if eb status &> /dev/null; then
    echo "🔄 기존 환경을 업데이트합니다..."
    eb deploy
else
    echo "🆕 새로운 환경을 생성합니다..."
    eb create --database.engine postgres --database.instance db.t3.micro
fi

echo "✅ 배포가 완료되었습니다!"
echo "🌐 애플리케이션 URL: $(eb status | grep CNAME | awk '{print $2}')"
