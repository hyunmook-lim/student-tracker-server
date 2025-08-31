# Student Tracker

학생 학습 분석 및 관리 시스템

## 🚀 배포

### Render 배포 URL

- **Production**: https://student-tracker.onrender.com
- **Health Check**: https://student-tracker.onrender.com/health

## 📋 API 엔드포인트

### 학생 분석 API

- `GET /api/students/{studentId}/analytics` - 전체 학생 분석
- `GET /api/students/{studentId}/classrooms/{classroomId}/analytics` - 특정 수업 분석

### 학생 관리 API

- `POST /api/students` - 학생 생성
- `GET /api/students/{id}` - 학생 조회
- `GET /api/students` - 전체 학생 목록
- `PATCH /api/students/{id}` - 학생 정보 수정
- `DELETE /api/students/{id}` - 학생 삭제
- `POST /api/students/login` - 학생 로그인

## 🛠 기술 스택

- **Backend**: Spring Boot 3.5.3, Java 17
- **Database**: H2 Database (개발), PostgreSQL (프로덕션)
- **Build Tool**: Gradle
- **Deployment**: Render

## 🔧 로컬 개발

```bash
# 프로젝트 클론
git clone <repository-url>
cd studentTracker

# 애플리케이션 실행
./gradlew bootRun

# 빌드
./gradlew build
```

## 📊 주요 기능

1. **학생 분석**

   - 전체 수업 수, 문제 수, 오답 수, 오답률
   - 대단원별, 소단원별, 난이도별 오답 통계
   - 오답률 Top3 대단원 및 소단원

2. **수업별 분석**

   - 특정 수업의 상세 분석
   - 대단원별 난이도별 오답률
   - 소단원별 오답률

3. **학생 관리**
   - 학생 등록, 수정, 삭제
   - 로그인 기능
   - 반 배정 관리
