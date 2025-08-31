# 프론트엔드-백엔드 연결 가이드

## 🌐 백엔드 API 엔드포인트

### 개발 환경

```
http://localhost:8080
```

### 프로덕션 환경

```
http://fit-math-prod.eba-3ezakhau.ap-northeast-2.elasticbeanstalk.com
```

## 🔧 프론트엔드 설정

### 1. 환경 변수 설정

#### Vite 프로젝트의 `.env` 파일

```env
# 개발 환경
VITE_API_BASE_URL=http://localhost:8080

# 프로덕션 환경
VITE_API_BASE_URL=http://fit-math-prod.eba-3ezakhau.ap-northeast-2.elasticbeanstalk.com
```

#### Vite 프로젝트의 `.env.production` 파일

```env
VITE_API_BASE_URL=http://fit-math-prod.eba-3ezakhau.ap-northeast-2.elasticbeanstalk.com
```

### 2. API 클라이언트 설정

#### `src/api/client.js` 또는 `src/api/client.ts`

```javascript
// JavaScript 버전
const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export const apiClient = {
  async get(endpoint) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include", // 쿠키 포함
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return response.json();
  },

  async post(endpoint, data) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify(data),
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return response.json();
  },

  async put(endpoint, data) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify(data),
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return response.json();
  },

  async delete(endpoint) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return response.json();
  },
};
```

#### TypeScript 버전

```typescript
// TypeScript 버전
const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

interface ApiResponse<T> {
  data?: T;
  message?: string;
  error?: string;
}

export const apiClient = {
  async get<T>(endpoint: string): Promise<T> {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return response.json();
  },

  async post<T>(endpoint: string, data: any): Promise<T> {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify(data),
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return response.json();
  },

  async put<T>(endpoint: string, data: any): Promise<T> {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify(data),
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return response.json();
  },

  async delete<T>(endpoint: string): Promise<T> {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return response.json();
  },
};
```

### 3. API 사용 예시

#### React 컴포넌트에서 사용

```jsx
import React, { useState, useEffect } from "react";
import { apiClient } from "../api/client";

function StudentList() {
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchStudents = async () => {
      try {
        setLoading(true);
        const data = await apiClient.get("/api/students");
        setStudents(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchStudents();
  }, []);

  const addStudent = async (studentData) => {
    try {
      const newStudent = await apiClient.post("/api/students", studentData);
      setStudents((prev) => [...prev, newStudent]);
    } catch (err) {
      setError(err.message);
    }
  };

  if (loading) return <div>로딩 중...</div>;
  if (error) return <div>에러: {error}</div>;

  return (
    <div>
      <h1>학생 목록</h1>
      {students.map((student) => (
        <div key={student.id}>
          {student.name} - {student.email}
        </div>
      ))}
    </div>
  );
}

export default StudentList;
```

### 4. Axios 사용 (선택사항)

Axios를 선호한다면:

```bash
npm install axios
```

```javascript
import axios from "axios";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
  headers: {
    "Content-Type": "application/json",
  },
});

// 요청 인터셉터
apiClient.interceptors.request.use(
  (config) => {
    // 토큰이 있다면 헤더에 추가
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터
apiClient.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    if (error.response?.status === 401) {
      // 인증 오류 처리
      localStorage.removeItem("token");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export default apiClient;
```

## 🔒 보안 설정

### 1. 프로덕션 환경에서 CORS 설정

백엔드 배포 후 Elastic Beanstalk 환경 변수에 프론트엔드 도메인을 추가:

```bash
eb setenv CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
```

### 2. HTTPS 설정

프로덕션에서는 HTTPS를 사용하도록 설정:

```javascript
// 프로덕션 환경에서는 HTTPS 강제
const API_BASE_URL = import.meta.env.PROD
  ? "https://fit-math-prod.eba-3ezakhau.ap-northeast-2.elasticbeanstalk.com"
  : "http://localhost:8080";
```

## 🧪 테스트

### 1. API 연결 테스트

```javascript
// 브라우저 콘솔에서 테스트
fetch(
  "http://fit-math-prod.eba-3ezakhau.ap-northeast-2.elasticbeanstalk.com/actuator/health"
)
  .then((response) => response.json())
  .then((data) => console.log("API 연결 성공:", data))
  .catch((error) => console.error("API 연결 실패:", error));
```

### 2. CORS 테스트

```javascript
// 프론트엔드에서 API 호출 테스트
fetch(
  "http://fit-math-prod.eba-3ezakhau.ap-northeast-2.elasticbeanstalk.com/api/students"
)
  .then((response) => response.json())
  .then((data) => console.log("학생 목록:", data))
  .catch((error) => console.error("에러:", error));
```

## 🚨 문제 해결

### 1. CORS 오류

- 백엔드 CORS 설정 확인
- 프론트엔드 도메인이 허용 목록에 포함되어 있는지 확인

### 2. 네트워크 오류

- 백엔드 서버가 실행 중인지 확인
- 방화벽 설정 확인
- Elastic Beanstalk 환경 상태 확인

### 3. 인증 오류

- JWT 토큰이 올바르게 전송되는지 확인
- 토큰 만료 시간 확인
