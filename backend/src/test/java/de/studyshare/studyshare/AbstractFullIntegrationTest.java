package de.studyshare.studyshare;

import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;


@Testcontainers 
public abstract class AbstractFullIntegrationTest extends AbstractDatabaseIntegrationTest { 

    @SuppressWarnings("resource")
    @Container
    static final MinIOContainer minioContainer = new MinIOContainer("minio/minio:latest")
            .withUserName("minioadmin")
            .withPassword("minioadmin");

    @DynamicPropertySource
    static void overrideMinioProperties(DynamicPropertyRegistry registry) {
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
            S3Client s3Client = S3Client.builder()
                    .endpointOverride(java.net.URI.create("http://" + minioContainer.getHost() + ":" + minioContainer.getMappedPort(9000)))
                    .region(software.amazon.awssdk.regions.Region.US_EAST_1)
                    .credentialsProvider(
                        software.amazon.awssdk.auth.credentials.StaticCredentialsProvider.create(
                            software.amazon.awssdk.auth.credentials.AwsBasicCredentials.create(
                                minioContainer.getUserName(),
                                minioContainer.getPassword()
                            )
                        )
                    )
                    .forcePathStyle(true)
                    .build();

            String testBucketName = "studyshare-uploads-test";

            boolean bucketExists = s3Client.listBuckets().buckets().stream()
                .anyMatch(b -> b.name().equals(testBucketName));

            if (bucketExists) {
                System.out.println("Minio Test bucket already exists: " + testBucketName);
            } else {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(testBucketName).build());
                System.out.println("Minio Test bucket created: " + testBucketName);
            }
            
        } catch (Exception e) {
            System.err.println("Failed to create Minio test bucket: " + e.getMessage());
        }
    }
}