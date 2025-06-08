package cn.oillusions.mcmods.aira.deepseek;

public class DeepSeekException extends Exception {
    private final StatusCode statusCode;

    public DeepSeekException(String message, int statusCode) {
        super(message);
        this.statusCode = StatusCode.ofErrorCode(statusCode);
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return toString();
    }

    @Override
    public String toString() {
        return "StatusCode: %s : ErrorReason: %s; Message: %s".formatted(
                statusCode.getStatusCode(),
                statusCode.getErrorReason(),
                super.getMessage());
    }

    public static enum StatusCode {
        INVALID_FORMAT(400, "Invalid Format"),
        AUTHENTICATION_FAILS(401, "Authentication Fails"),
        INSUFFICIENT_BALANCE(402, "Insufficient Balance"),
        INVALID_PARAMETERS(422, "Invalid Parameters"),
        RATE_LIMIT_REACHED(429, "Rate Limit Reached"),
        SERVER_ERROR(500, "Server Error"),
        SERVER_OVERLOADED(503, "Server Overloaded"),
        UNIVERSAL_ERROR_INTERPRETATION(-1, "Universal Error Interpretation")
        ;
        private final int statusCode;
        private final String errorReason;

        StatusCode(int statusCode, String errorReason) {
            this.errorReason = errorReason;
            this.statusCode = statusCode;
        }

        public static StatusCode ofErrorCode(int statusCode) {
            switch (statusCode) {
                case 400 -> {
                    return StatusCode.INVALID_FORMAT;
                }
                case 401 -> {
                    return StatusCode.AUTHENTICATION_FAILS;
                }
                case 402 -> {
                    return StatusCode.INSUFFICIENT_BALANCE;
                }
                case 422 -> {
                    return StatusCode.INVALID_PARAMETERS;
                }
                case 429 -> {
                    return StatusCode.RATE_LIMIT_REACHED;
                }

                case 500 -> {
                    return StatusCode.SERVER_ERROR;
                }

                case 503 -> {
                    return StatusCode.SERVER_OVERLOADED;
                }
                default -> {
                    return UNIVERSAL_ERROR_INTERPRETATION;
                }
            }
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getErrorReason() {
            return errorReason;
        }
    }

}
