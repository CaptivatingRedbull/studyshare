package de.studyshare.studyshare.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Exception;


/**
 * Service for handling AWS S3 operations.
 */
@Service
public class AwsService {
    @Autowired
    private S3Client s3Client;

    /**
     * Uploads a file to an S3 bucket.
     *
     * @param bucketName  the name of the S3 bucket
     * @param keyName     the key under which the file will be stored
     * @param fileSize    the size of the file in bytes
     * @param inputStream the InputStream of the file to upload
     */
    public void uploadFile(String bucketName,
            String keyName,
            long fileSize,
            InputStream inputStream) {
        s3Client.putObject(builder -> builder
                .bucket(bucketName)
                .key(keyName)
                .build(),
            software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, fileSize));
    }

    /**
     * Lists all files in a specified S3 bucket.
     *
     * @param bucketName the name of the S3 bucket
     * @return a list of file names in the bucket
     */
    public void deleteFile(
            final String bucketName,
            final String keyName) throws S3Exception {
        s3Client.deleteObject(
            software.amazon.awssdk.services.s3.model.DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build()
        );
    }

    /**
     * Lists all files in a specified S3 bucket.
     *
     * @param bucketName the name of the S3 bucket
     * @return a list of file names in the bucket
     */
    public ByteArrayOutputStream downloadFile(
            final String bucketName,
            final String keyName) throws IOException, S3Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        s3Client.getObject(
            software.amazon.awssdk.services.s3.model.GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build(),
            software.amazon.awssdk.core.sync.ResponseTransformer.toOutputStream(outputStream)
        );
        return outputStream;
    }
}
