#!/bin/bash

# Gradle 빌드 (테스트를 제외한 빌드)
./gradlew :app:external-api:build -x test

# Docker 이미지 빌드 및 푸시
DOCKER_USERNAME=seunghso
DOCKER_IMAGE_NAME=peerna

# Linux AMD64 용 이미지 빌드
docker build -f Dockerfile_external-api --build-arg DEPENDENCY=build/dependency -t $DOCKER_USERNAME/$DOCKER_IMAGE_NAME --platform linux/amd64 .

# 이미지 푸시
docker push $DOCKER_USERNAME/$DOCKER_IMAGE_NAME

