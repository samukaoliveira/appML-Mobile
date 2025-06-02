package com.example.appml.models;

public class UsuarioLoginRequest {
    private Usuario usuario;

    public UsuarioLoginRequest(String email, String password) {
        this.usuario = new Usuario(email, password);
    }

    public static class Usuario {
        private String email;
        private String password;

        public Usuario(String email, String password) {
            this.email = email;
            this.password = password;
        }

    }
}
