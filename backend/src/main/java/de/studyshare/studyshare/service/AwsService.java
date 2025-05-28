package de.studyshare.studyshare.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

/**
 * Service for handling AWS S3 operations.
 */
@Service
public class AwsService {
    @Autowired
    private AmazonS3 s3Client;

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
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileSize);
        s3Client.putObject(bucketName, keyName, inputStream, metadata);
    }

    /**
     * Lists all files in a specified S3 bucket.
     *
     * @param bucketName the name of the S3 bucket
     * @return a list of file names in the bucket
     */
    public void deleteFile(
            final String bucketName,
            final String keyName) throws AmazonClientException {
        s3Client.deleteObject(bucketName, keyName);
    }

    /**
     * Lists all files in a specified S3 bucket.
     *
     * @param bucketName the name of the S3 bucket
     * @return a list of file names in the bucket
     */
    public ByteArrayOutputStream downloadFile(
            final String bucketName,
            final String keyName) throws IOException, AmazonClientException {
        S3Object s3Object = s3Client.getObject(bucketName, keyName);
        InputStream inputStream = s3Object.getObjectContent();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        int len;
        byte[] buffer = new byte[4096];
        while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        return outputStream;
    }
}
