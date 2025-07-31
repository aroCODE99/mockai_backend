package com.mockAi.MOCAI.Dtos.Response;

public record DataResponse<T>(
    T Data,
    String message,
    String timeStamp
) { }
