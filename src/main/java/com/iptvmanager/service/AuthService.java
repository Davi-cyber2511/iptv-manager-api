package com.iptvmanager.service;

import com.iptvmanager.domain.Usuario;
import com.iptvmanager.dto.LoginRequestDTO;
import com.iptvmanager.dto.LoginResponseDTO;
import com.iptvmanager.dto.RegisterRequestDTO;
import com.iptvmanager.dto.RegisterResponseDTO;
import com.iptvmanager.repository.UsuarioRepository;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrDataFactory;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.iptvmanager.security.JwtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final String ISSUER = "IPTV Manager";
    private static final HashingAlgorithm ALGORITHM = HashingAlgorithm.SHA1;
    private static final int DIGITS = 6;
    private static final int PERIOD = 30;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final SecretGenerator secretGenerator;
    private final QrDataFactory qrDataFactory;
    private final QrGenerator qrGenerator;
    private final CodeVerifier codeVerifier;
    private final JwtService jwtService;

    @Autowired
    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;

        this.secretGenerator = new DefaultSecretGenerator();
        this.qrDataFactory = new QrDataFactory(ALGORITHM, DIGITS, PERIOD);
        this.qrGenerator = new ZxingPngQrGenerator();
        this.codeVerifier = new DefaultCodeVerifier(
                new DefaultCodeGenerator(),
                new SystemTimeProvider()
        );
    }
    @Transactional
    public RegisterResponseDTO registerNewUser(RegisterRequestDTO request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado.");
        }

        Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .nome(request.getNome())
                .role(request.getRole())
                .twoFactorEnabled(false)
                .twoFactorSecret(null)
                .build();

        Usuario salvo = usuarioRepository.save(usuario);

        return RegisterResponseDTO.builder()
                .id(salvo.getId())
                .email(salvo.getEmail())
                .nome(salvo.getNome())
                .role(salvo.getRole())
                .build();
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getSenha()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado.")
                );

        String token = jwtService.generateToken(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getRole()
        );

        return LoginResponseDTO.builder()
                .token(token)
                .userId(usuario.getId())
                .userName(usuario.getNome())
                .userRole(usuario.getRole())
                .twoFactorRequired(usuario.isTwoFactorEnabled())
                .build();
    }

    @Transactional
    public LoginResponseDTO enableTwoFactor(String userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        String newSecret = generateNewSecret();
        usuario.setTwoFactorSecret(newSecret);
        usuario.setTwoFactorEnabled(true);
        usuarioRepository.save(usuario);

        String qrCodeImage = generateQrCodeImage(newSecret, usuario.getEmail());

        return LoginResponseDTO.builder()
                .userId(usuario.getId())
                .userName(usuario.getNome())
                .userRole(usuario.getRole())
                .twoFactorRequired(true)
                .twoFactorQrCodeImageUrl(qrCodeImage)
                .build();
    }

    @Transactional
    public void disableTwoFactor(String userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        usuario.setTwoFactorEnabled(false);
        usuario.setTwoFactorSecret(null);
        usuarioRepository.save(usuario);
    }

    public String generateNewSecret() {
        return secretGenerator.generate();
    }

    public String generateQrCodeImage(String secret, String username) {
        QrData qrData = qrDataFactory.newBuilder()
                .label(username)
                .secret(secret)
                .issuer(ISSUER)
                .build();

        try {
            byte[] qrCodeImageBytes = qrGenerator.generate(qrData);
            return Utils.getDataUriForImage(qrCodeImageBytes, qrGenerator.getImageMimeType());
        } catch (QrGenerationException exception) {
            throw new IllegalStateException(
                    "Não foi possível gerar o QR Code. Verifique a configuração do TOTP.",
                    exception
            );
        }
    }

    public boolean verifyCode(String code, String secret) {
        if (code == null || code.isBlank()) {
            return false;
        }
        if (secret == null || secret.isBlank()) {
            return false;
        }
        return codeVerifier.isValidCode(secret, code);
    }
}