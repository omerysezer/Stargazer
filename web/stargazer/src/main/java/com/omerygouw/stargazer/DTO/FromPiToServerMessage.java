package com.omerygouw.stargazer.DTO;

import lombok.Builder;

@Builder
public record FromPiToServerMessage(String sessionId, String messageType, String message, String instructionId){
}
