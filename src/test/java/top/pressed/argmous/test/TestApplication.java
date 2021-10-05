package top.pressed.argmous.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import top.pressed.argmous.exception.ParamCheckException;

@SpringBootApplication(scanBasePackages = "top.pressed.argmous")
@RestController
@RequestMapping("/")
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @RestControllerAdvice
    public static class ErrorHandler {
        @ExceptionHandler(ParamCheckException.class)
        public String error(Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/test")
    public String testValidate(String s, Integer i) {
        return "success";
    }
}
