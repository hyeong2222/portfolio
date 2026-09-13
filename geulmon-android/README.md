# 글몬 (Geulmon) — 안드로이드 앱

동네에 숨은 2D 글몬을 찾아 글구슬로 잡는 AR 수집 게임. 유정국어학원용.

- 게임 본체: `app/src/main/assets/www/index.html` (HTML5 단일 파일 — 웹에서도 그대로 실행됨)
- 안드로이드 셸: `app/src/main/java/kr/co/kangyu/geulmon/MainActivity.java` (WebView + 카메라·위치 권한 처리)
- 자동 빌드: `.github/workflows/build.yml` (GitHub에 올리면 .aab / .apk 생성)
- 서명키: `keystore/upload.jks` (비밀번호 `geulmon2026`, 별칭 `upload`) — **레포는 반드시 비공개(Private)로, 이 파일은 따로 백업**
- 개인정보처리방침: `docs/privacy.html`
- 스토어 아이콘(512px): `docs/playstore_icon_512.png`

자세한 출시 절차는 함께 드린 `글몬_출시_가이드.md`를 보세요.

## 게임 내용 바꾸기
글몬 추가·수정은 `index.html` 안의 `MONSTERS` 배열(이름, 희귀도, 색, 사자성어 뜻·예문)만 고치면 됩니다.
파일을 고친 뒤 GitHub에 다시 올리면 새 .aab가 자동으로 만들어집니다. 스토어에 업데이트를 올릴 때는
`app/build.gradle`의 `versionCode`를 1씩 올리고 `versionName`을 바꿔 주세요.
