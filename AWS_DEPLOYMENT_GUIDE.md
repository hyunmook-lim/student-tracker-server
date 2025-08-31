# AWS 배포 가이드

## 📋 사전 준비사항

### 1. AWS 계정 및 권한

- AWS 계정이 필요합니다
- IAM 사용자에게 Elastic Beanstalk 권한이 필요합니다
- AWS CLI가 설치되어 있어야 합니다

### 2. 로컬 환경 설정

```bash
# AWS CLI 설치
curl "https://awscli.amazonaws.com/AWSCLIV2.pkg" -o "AWSCLIV2.pkg"
sudo installer -pkg AWSCLIV2.pkg -target /

# EB CLI 설치
pip install awsebcli

# AWS 자격 증명 설정
aws configure
```

## 🚀 배포 방법

### 방법 1: 자동 스크립트 사용 (권장)

```bash
./deploy-aws.sh
```

### 방법 2: 수동 배포

```bash
# 1. 애플리케이션 빌드
./gradlew clean build -x test

# 2. EB 초기화 (처음 실행 시)
eb init

# 3. 환경 생성
eb create --database.engine postgres --database.instance db.t3.micro

# 4. 배포
eb deploy
```

## 🔧 환경 변수 설정

AWS Elastic Beanstalk 콘솔에서 다음 환경 변수를 설정하세요:

### 필수 환경 변수

- `SPRING_PROFILES_ACTIVE`: prod
- `SERVER_PORT`: 8080

### 데이터베이스 환경 변수 (RDS 사용 시)

- `RDS_HOSTNAME`: 데이터베이스 호스트
- `RDS_PORT`: 5432
- `RDS_DB_NAME`: 데이터베이스 이름
- `RDS_USERNAME`: 데이터베이스 사용자명
- `RDS_PASSWORD`: 데이터베이스 비밀번호

## 🌐 배포 후 확인

1. **애플리케이션 URL 확인**

   ```bash
   eb status
   ```

2. **로그 확인**

   ```bash
   eb logs
   ```

3. **환경 상태 확인**
   ```bash
   eb health
   ```

## 🔄 업데이트 배포

코드 변경 후 배포:

```bash
./gradlew clean build -x test
eb deploy
```

## 🗑️ 환경 삭제

```bash
eb terminate
```

## 💰 비용 최적화

### 무료 티어 사용

- t2.micro 인스턴스 사용
- RDS t3.micro 사용
- S3 표준 스토리지 사용

### 비용 모니터링

- AWS Cost Explorer 활성화
- 예산 알림 설정

## 🔒 보안 설정

### HTTPS 설정

```bash
eb config
# Load Balancer에서 HTTPS 리스너 추가
```

### 환경 변수 보안

- 민감한 정보는 AWS Systems Manager Parameter Store 사용
- 환경 변수로 직접 설정하지 않기

## 📊 모니터링

### CloudWatch 설정

- 로그 그룹 생성
- 메트릭 알림 설정
- 대시보드 구성

### Health Check

- `/actuator/health` 엔드포인트 활용
- 커스텀 헬스 체크 구현

## 🆘 문제 해결

### 일반적인 문제들

1. **빌드 실패**

   ```bash
   ./gradlew clean build --info
   ```

2. **데이터베이스 연결 실패**

   - 보안 그룹 설정 확인
   - 환경 변수 확인

3. **메모리 부족**
   - JVM 힙 크기 조정
   - 인스턴스 타입 업그레이드

### 로그 확인

```bash
eb logs --all
```

## 📞 지원

문제가 발생하면 다음을 확인하세요:

1. AWS Elastic Beanstalk 콘솔의 이벤트 로그
2. 애플리케이션 로그
3. 시스템 로그
