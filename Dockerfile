# 베이스 이미지 설정
FROM openjdk:17

# 작업 디렉토리 설정
WORKDIR /app

# 애플리케이션 jar 파일을 이미지에 복사
COPY ./build/libs/backend-0.0.1-SNAPSHOT.jar /app/app.jar

# 컨테이너가 시작되었을 때 실행될 명령어 설정
ENTRYPOINT ["java","-jar","/app/app.jar"]
