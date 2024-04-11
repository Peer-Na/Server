## ⭐ Stack
<img src="https://img.shields.io/badge/SpringBoot-6db33f?style=flat&logo=Spring Boot&logoColor=FFFFFF"/><img src="https://img.shields.io/badge/Java-e6e6fa?style=flat&logo=OpenJDK&logoColor=000000"/>
<img src="https://img.shields.io/badge/MariaDB-003545?style=flat&logo=MariaDB&logoColor=FFFFFF"/>
<img src="https://img.shields.io/badge/Redis-DC382D?style=flat&logo=Redis&logoColor=FFFFFF"/>
<img src="https://img.shields.io/badge/AWS-232F3E?style=flat&logo=Amazon AWS&logoColor=FFFFFF"/>
<img src="https://img.shields.io/badge/Docker-FFFFFF?style=flat&logo=Docker&logoColor=2496ED"/>
<img src="https://img.shields.io/badge/Thymeleaf-FFFFFF?style=flat&logo=Thymeleaf&logoColor=005F0F"/>

## 📌 Project Structure
<img width="671" alt="Untitled (34)" src="https://github.com/Peer-Na/Server/assets/45088611/c4bda9bc-4141-4756-8cdb-b0028576b965">

(이미지 출처: [우아한 기술 블로그](https://techblog.woowahan.com/2637/))

멀티 모듈 프로젝트로 구성하였습니다.
- independent available: **infra-mariadb**, **infra-redis**
- system domain : **domain-integrated** (분리 중)
- application: **app-external-api**, **app-match** (추가예정), **app-chat** (추가예정)


## 📚 Entity Relationship Diagram
![Untitled (17)](https://github.com/PeerNA/Backend/assets/45088611/d8f5c586-8a30-4278-a058-abe729bc0083)

# 핵심 기능 소개
## 2. GPT와 학습 (GPT 면접관)
![2024-03-1010 44 54-ezgif com-video-to-gif-converter (2)](https://github.com/Peer-Na/Server/assets/45088611/83fa44d0-623f-46e9-a896-f993a8bd22e8)

```
질문에 대한 답변을 하면 GPT가 답변에 대해 부족한 점을 피드백하거나 새로운 꼬리질문을 합니다.
이후에 사용자는 꼬리질문에 대답하거나 모르는 것에 대해 물어볼 수 있습니다.
```

## 3. 사람과 함께 학습 (동료학습)
<img width="1710" alt="Untitled (30)" src="https://github.com/Peer-Na/Server/assets/45088611/4cd84f38-7b2d-4845-b822-6815c0c052d7">

```
GPT가 아닌 사람과도 학습할 수 있습니다.
동료 매칭은 관심사(운영체제, 네트워크, 알고리즘 등)와 레이팅, 대기시간을 참고하는 매칭 시스템을 통해서 이루어집니다.
```

## 4. 빈출 키워드
<img width="655" alt="Untitled (31)" src="https://github.com/Peer-Na/Server/assets/45088611/4e51a201-0773-454f-8f3e-ab044d155f9f">

```
빈출 키워드는  학습 보조 수단입니다.
모르는 것을 질문할 때, 혹은 대답할 때 사용자는 빈출 키워드의 도움을 받을 수 있습니다.
빈출 키워드는 제출된 사용자들의 답변을 토큰화하여 통계를 내었을 때, 가장 많은 빈도로 사용된 단어 3개를 가리킵니다.
```

## 5. 자동 깃허브 레포지토리 업데이트
<img width="849" alt="Untitled (32)" src="https://github.com/Peer-Na/Server/assets/45088611/c616ff2d-ad42-4568-9f2c-f2c3c4c306b7">

```
제출한 답변은 사용자의 Github 레포지토리에 자동으로 업데이트 됩니다.
Github OAuth 와 Rest API for Github Repository 를 사용하였습니다.
이를 통해 자신만의 답변을 완성해 나갈 수 있으며 지식공유 및 저장이 가능합니다.
```

## 6. 학습 이력 조회
<img width="1710" alt="Untitled (33)" src="https://github.com/Peer-Na/Server/assets/45088611/3461c78a-a0b0-4ab9-acd7-6205b7360574">

```
사용자의 답변, GPT 혹은 동료와의 대화들은 저장되고 이후에도 조회할 수 있습니다.
```

## 7. 유저 및 인증/인가 모듈
- Github OAuth2 회원가입/로그인
- Session 기반 유저 인증

## 8. 클라이언트 개발
- Thymeleaf
- Tailwind CSS

