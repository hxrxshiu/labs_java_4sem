package com.example.lab1.Exception;

import java.util.List;

public record ErrorResponse(String message, List<String> details) {
}