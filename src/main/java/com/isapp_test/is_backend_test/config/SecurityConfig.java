package com.isapp_test.is_backend_test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // ← セキュリティ関連の設定を定義するクラス
public class SecurityConfig {

    @Bean // ← Spring Security の「フィルタチェーン」を定義する Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {
        http
            // ✅ CSRF 対策を無効化
            // REST API (特にフロントとバックを分離する構成) では通常不要なので無効にする
            .csrf(csrf -> csrf.disable())

            // ✅ CORS を有効化
            // → CorsConfig で定義した corsConfigurationSource() が自動的に適用される
            .cors(cors -> {})

            // ✅ リクエストごとの認可ルールを定義
            .authorizeHttpRequests(auth -> auth
                // プリフライトリクエスト (OPTIONS) はすべて許可
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // その他のリクエストも全部許可（最初の疎通確認用）
                // 本番運用では「特定の API だけ許可」「ログイン必須」など細かく制御する
                .anyRequest().permitAll()
            );

        // ✅ 最終的にフィルタチェーンを返す
        return http.build();
    }
}
