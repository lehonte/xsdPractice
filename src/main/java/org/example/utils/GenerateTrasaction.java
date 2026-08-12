package org.example.utils;

import org.example.entities.Transaction;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class GenerateTrasaction {

    public String generateTransactionNumber(Transaction transaction) {
        try {
            String number = String.valueOf(transaction.getId()) +
                    transaction.getAccount() +
                    transaction.getAmount() +
                    transaction.getType() +
                    transaction.getDate();

            MessageDigest digest = MessageDigest.getInstance("SHA-256"); //выбираем алгоритм хеширования

            byte[] hashBytes = digest.digest(number.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            throw new RuntimeException("Не удалось сгенерировать номер транзакции", e);
        }
    }
}
