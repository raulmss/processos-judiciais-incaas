package com.incaas.tjrn.security.auth;

import com.incaas.tjrn.security.user.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para login e registro de usuários e administradores")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register-user")
    @Operation(
            summary = "Registrar novo usuário padrão",
            description = "Cria uma nova conta de usuário com role USER",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Usuário registrado com sucesso",
                            content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Erro ao registrar usuário")
            }
    )
    public ResponseEntity<AuthenticationResponse> registerUser(
            @RequestBody @Valid RegisterRequest request) {
        AuthenticationResponse response = authenticationService.register(request, Role.USER);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/register-admin")
    @Operation(
            summary = "Registrar novo administrador",
            description = "Cria uma nova conta com permissões administrativas (role ADMIN)",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Administrador registrado com sucesso",
                            content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Erro ao registrar administrador")
            }
    )
    public ResponseEntity<AuthenticationResponse> registerAdmin(
            @RequestBody @Valid RegisterRequest request) {
        AuthenticationResponse response = authenticationService.register(request, Role.ADMIN);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/authenticate")
    @Operation(
            summary = "Autenticar usuário",
            description = "Realiza login e retorna um token JWT válido",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Autenticação bem-sucedida",
                            content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
            }
    )
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
