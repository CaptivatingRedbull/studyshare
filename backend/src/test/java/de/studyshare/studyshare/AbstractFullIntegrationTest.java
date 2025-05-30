package de.studyshare.studyshare;

import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
// Potentially import S3 client for bucket creation
// import com.amazonaws.auth.AWSStaticCredentialsProvider;
// import com.amazonaws.auth.BasicAWSCredentials;
// import com.amazonaws.client.builder.AwsClientBuilder;
// import com.amazonaws.services.s3.AmazonS3;
// import com.amazonaws.services.s3.AmazonS3ClientBuilder;
// import org.junit.jupiter.api.BeforeAll;


@Testcontainers // This might be redundant if inheriting from a @Testcontainers base class
public abstract class AbstractFullIntegrationTest extends AbstractDatabaseIntegrationTest { // Inherits MariaDB

    @SuppressWarnings("resource")
    @Container
    static final MinIOContainer minioContainer = new MinIOContainer("minio/minio:latest")
            .withUserName("minioadmin")
            .withPassword("minioadmin");

    @DynamicPropertySource
    static void overrideMinioProperties(DynamicPropertyRegistry registry) {
        // MariaDB properties are already handled by the parent class's @DynamicPropertySource

        // Minio (S3) properties
        registry.add("s3.endpoint", () -> "http://" + minioContainer.getHost() + ":" + minioContainer.getMappedPort(9000));
        registry.add("s3.access-key", minioContainer::getUserName);
        registry.add("s3.secret-key", minioContainer::getPassword);
        registry.add("s3.bucket-name", () -> "studyshare-uploads-test");
        registry.add("s3.region", () -> "us-east-1");
    }

    @BeforeAll
    static void setupMinio() {
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration(
                                    "http://" + minioContainer.getHost() + ":" + minioContainer.getMappedPort(9000),
                                    "us-east-1"
                            )
                    )
                    .withCredentials(new AWSStaticCredentialsProvider(
                            new BasicAWSCredentials(minioContainer.getUserName(), minioContainer.getPassword()))
                    )
                    .withPathStyleAccessEnabled(true)
                    .build();

            String testBucketName = "studyshare-uploads-test";
            if (!s3Client.doesBucketExistV2(testBucketName)) {
                s3Client.createBucket(testBucketName);
                System.out.println("Minio Test bucket created: " + testBucketName);
            }
        } catch (Exception e) {
            System.err.println("Failed to create Minio test bucket: " + e.getMessage());
        }
    }
}