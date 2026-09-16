package com.iptvmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private String userId;
    private String userName;
    private String userRole;
    private boolean twoFactorRequired; // Indica se o 2FA é necessário
    private String twoFactorQrCodeImageUrl; // URL da imagem QR Code para setup inicial
}
