package com.example.holidayswap.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {
    private final AmazonS3 amazonS3client;

    @Value("${amazon.s3.bucket.name}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String result = null;
        if (multipartFile != null) {
            File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(multipartFile.getBytes());
            } catch (IOException e) {
                log.error("Error converting multipartFile to file", e);
            }
            String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
            amazonS3client.putObject(new PutObjectRequest(bucketName, fileName, file));
            result = amazonS3client.getUrl(bucketName, fileName).toString();
            Files.delete(Path.of(Objects.requireNonNull(multipartFile.getOriginalFilename())));
        }
        return result;
    }

    @Override
    public String createQRCode(String link) throws IOException, WriterException {
        String result = null;
        BitMatrix bitMatrix = new QRCodeWriter().encode(link, BarcodeFormat.QR_CODE, 500, 500);
        File qrCodeFile = new File(UUID.randomUUID() + "_qrcode.png");
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", qrCodeFile.toPath());
        String fileName = UUID.randomUUID() + "_qrcode.png";
        amazonS3client.putObject(new PutObjectRequest(bucketName, fileName, qrCodeFile));
        result = amazonS3client.getUrl(bucketName, fileName).toString();
        Files.delete(qrCodeFile.toPath());
        return result;
    }

}
