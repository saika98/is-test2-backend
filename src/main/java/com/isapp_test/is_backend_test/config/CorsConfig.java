package com.isapp_test.is_backend_test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration // ← このクラスは「設定クラス」として Spring に読み込まれる
public class CorsConfig {

    @Bean // ← Spring が管理する「Bean（部品）」として登録される
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ 許可するオリジン（S3 バケットの URL）
        // ここからのリクエストなら受け入れる、という意味
        // config.setAllowedOrigins(List.of(
        //     "http://is-test-frontend.s3-website.ap-northeast-3.amazonaws.com" // S3からの接続を許可(elastic beanstalkにデプロイするとき用)
        // ));
        config.setAllowedOrigins(List.of(
            "http://localhost:5174",
            "http://127.0.0.1:5174" // ローカルからの接続を許可
        ));

        // ✅ 許可する HTTP メソッド
        // REST API で使う GET/POST/PUT/DELETE と、プリフライトリクエスト用の OPTIONS を許可
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // ✅ 許可する HTTP ヘッダ
        // "*" = すべてのヘッダを許可
        config.setAllowedHeaders(List.of("*"));

        // ✅ Cookie を跨いだ通信を許可するか
        // true にすると「認証が必要なリクエスト（例: Cookie/Authorizationヘッダ）」も許可できる
        config.setAllowCredentials(false);

        // ✅ この CORS 設定を「全てのパス（/**）」に適用する
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
