package com.omerygouw.stargazer.DTO;

import lombok.Builder;

@Builder
public record FromServerToPiMessage(String instruction, String instructionData, String instructionId) {
}
