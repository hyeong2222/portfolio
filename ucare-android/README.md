# 유정국어 U-Care — 안드로이드 설치용 셸 앱

유정국어 U-Care 모바일 LMS를 홈 화면 아이콘으로 설치해서 쓰기 위한 안드로이드 앱입니다.

## 이 앱이 하는 일

화면과 기능은 모두 **서버에 올라간 사이트**가 담당합니다. 이 앱은 그 사이트를
**Trusted Web Activity(TWA)** 로 띄우는 껍데기입니다.

```
https://yujeong-ucare-mobile.kangyu-co-kr.chatgpt.site/
```

- LMS 소스 코드와 학생 자료는 이 앱에 들어 있지 않습니다. 주소와 아이콘만 들어 있습니다.
- WebView가 아니라 **설치된 크롬 엔진**으로 띄웁니다. 구글 계정 로그인이 WebView에서 막히는
  문제(카카오톡·네이버 인앱 브라우저와 같은 증상)를 피하기 위한 선택입니다.
- 사이트를 고치면 앱을 다시 설치하지 않아도 반영됩니다. 앱을 다시 빌드할 일은
  주소나 아이콘이 바뀔 때뿐입니다.

## 주소줄 없이 전체화면으로 띄우려면

TWA는 사이트가 이 앱을 자기 대리인으로 인정해야 주소줄을 숨깁니다. 인정하지 않으면
동작은 그대로이고 위쪽에 얇은 주소줄이 남습니다.

1. 빌드 결과의 `dist/ucare-signing-info.txt` 에서 서명 인증서 **SHA-256 지문**을 확인합니다.
2. 사이트가 아래 내용을 `/.well-known/assetlinks.json` 으로 응답하게 합니다.

```json
[{
  "relation": ["delegate_permission/common.handle_all_urls"],
  "target": {
    "namespace": "android_app",
    "package_name": "kr.co.kangyu.ucare",
    "sha256_cert_fingerprints": ["<위에서 확인한 SHA-256 지문>"]
  }
}]
```

Next.js(vinext) 쪽에서는 `app/.well-known/assetlinks.json/route.ts` 로 라우트를 하나
추가해 그 JSON을 `application/json` 으로 돌려주면 됩니다.

3. 지문은 **서명키가 바뀌면 같이 바뀝니다.** 임시 키로 빌드하면 매번 달라지므로,
   이 단계를 쓸 거면 아래 '서명키' 의 고정 키 방식을 먼저 적용하세요.

## 서명키

이 레포는 공개 상태이므로 keystore 파일을 두지 않습니다. 빌드할 때 순서대로 찾습니다.

1. 레포 시크릿 `UCARE_KEYSTORE_BASE64` — 있으면 이것을 씁니다 (권장)
2. `keystore/upload.jks` 파일 — 비공개 레포에서 쓸 때
3. 둘 다 없으면 **매 빌드마다 임시 키를 새로 생성** — 설치·테스트는 되지만
   덮어쓰기 업데이트와 assetlinks 등록에는 쓸 수 없습니다

고정 키를 만들어 시크릿에 넣는 방법:

```
keytool -genkeypair -v -keystore upload.jks -storetype PKCS12 \
  -alias upload -keyalg RSA -keysize 2048 -validity 10000
base64 -w0 upload.jks        # 출력을 UCARE_KEYSTORE_BASE64 시크릿에 붙여넣기
```

비밀번호를 기본값(`ucare2026`)과 다르게 정했다면 `UCARE_KEYSTORE_PASSWORD`,
`UCARE_KEY_PASSWORD`, `UCARE_KEY_ALIAS` 시크릿도 함께 넣으세요.

## 주소 바꾸기

`app/src/main/res/values/strings.xml` 의 `host` 와 `launch_url`,
그리고 `asset_statements` 안의 주소를 같이 고치면 됩니다.

## 사이트 접근 설정

사이트가 '소유자 전용 비공개'이면 앱을 설치해도 원장 계정 외에는 로그인할 수 없습니다.
학부모·학생이 쓰려면 호스팅의 사이트 접근 설정을 먼저 열어야 합니다.
