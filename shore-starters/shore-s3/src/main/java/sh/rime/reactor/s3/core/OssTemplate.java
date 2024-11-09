package sh.rime.reactor.s3.core;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import sh.rime.reactor.core.util.BeanUtil;
import sh.rime.reactor.s3.bean.OssToken;
import sh.rime.reactor.s3.bean.ResourcePathConfig;
import sh.rime.reactor.s3.props.OssProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.Credentials;
import software.amazon.awssdk.services.sts.model.GetSessionTokenRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * oss 模板
 *
 * @author youta
 **/
@SuppressWarnings("unused")
@Slf4j
public class OssTemplate implements InitializingBean {

    private final OssProperties ossProperties;

    private S3Client s3Client;
    private StsClient stsClient;
    private S3Presigner s3Presigner;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param ossProperties the oss properties
     */
    public OssTemplate(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    /**
     * 创建bucket
     *
     * @param bucketName bucket名称
     */
    public void createBucket(String bucketName) {
        s3Client.createBucket(CreateBucketRequest.builder()
                .bucket(bucketName)
                .build());
    }

    /**
     * 获取全部bucket
     *
     * @return bucket列表
     */
    public List<Bucket> getAllBuckets() {
        return s3Client.listBuckets().buckets();
    }

    /**
     * get bucket
     *
     * @param bucketName bucket名称
     * @return bucket
     */
    public Optional<Bucket> getBucket(String bucketName) {
        return this.getAllBuckets()
                .stream()
                .filter(b -> b.name().equals(bucketName))
                .findFirst();
    }

    /**
     * remove bucket
     *
     * @param bucketName bucket名称
     */
    public void removeBucket(String bucketName) {
        s3Client.deleteBucket(DeleteBucketRequest.builder()
                .bucket(bucketName)
                .build());
    }

    /**
     * 根据文件前置查询文件
     *
     * @param bucketName bucket名称
     * @param prefix     前缀
     * @return 文件列表
     */
    public List<S3Object> getAllObjectsByPrefix(String bucketName, String prefix) {
        return s3Client.listObjects(ListObjectsRequest.builder()
                        .bucket(bucketName)
                        .prefix(prefix)
                        .build())
                .contents();
    }


    /**
     * 获取文件上传外链，只用于上传
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param minutes    过期时间，单位分钟,请注意该值必须小于7天
     * @return url
     */
    public String getPutObjectURL(String bucketName, String objectName, int minutes) {
        return getPutObjectURL(bucketName, objectName, Duration.ofMinutes(minutes));
    }

    /**
     * 获取文件上传外链，只用于上传
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param expires    过期时间,请注意该值必须小于7天
     * @return url
     */
    public String getPutObjectURL(String bucketName, String objectName, Duration expires) {
        return putObjectURL(bucketName, objectName, expires);
    }

    /**
     * 获取文件外链
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param minutes    过期时间，单位分钟,请注意该值必须小于7天
     * @return url
     * HttpMethod method)
     */
    public String getObjectURL(String bucketName, String objectName, int minutes) {
        return getObjectURL(bucketName, objectName, Duration.ofMinutes(minutes));
    }

    /**
     * 获取文件外链(下载)
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param expires    过期时间，请注意该值必须小于7天
     * @return url
     */
    public String getObjectURL(String bucketName, String objectName, Duration expires) {
        // Set the pre-signed URL to expire after `expires`.
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .build();

        GetObjectPresignRequest preSignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expires)
                .getObjectRequest(objectRequest)
                .build();
        return s3Presigner.presignGetObject(preSignRequest)
                .url()
                .toString();
    }

    /**
     * 获取文件外链(上传)
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param expires    过期时间，请注意该值必须小于7天
     * @return url
     */
    public String putObjectURL(String bucketName, String objectName, Duration expires) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .build();

        PutObjectPresignRequest preSignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(expires)
                .putObjectRequest(objectRequest)
                .build();
        return s3Presigner.presignPutObject(preSignRequest)
                .url()
                .toString();
    }

    /**
     * 获取文件URL
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return url
     */
    public String getObjectURL(String bucketName, String objectName) {
        return this.getObjectURL(bucketName, objectName, Duration.ofDays(7));
    }

    /**
     * 获取文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return 二进制流
     */
    public GetObjectResponse getObject(String bucketName, String objectName) {
        return s3Client.getObject(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectName)
                        .build())
                .response();
    }

    /**
     * 上传文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param stream     文件流
     * @throws IOException IOException
     */
    public void putObject(String bucketName, String objectName, InputStream stream) throws IOException {
        putObject(bucketName, objectName, stream, stream.available(), "application/octet-stream");
    }

    /**
     * 上传文件 指定 contextType
     *
     * @param bucketName  bucket名称
     * @param objectName  文件名称
     * @param stream      文件流
     * @param contextType 文件类型
     * @throws IOException IOException
     */
    public void putObject(String bucketName, String objectName, String contextType, InputStream stream)
            throws IOException {
        putObject(bucketName, objectName, stream, stream.available(), contextType);
    }

    /**
     * 上传文件
     *
     * @param bucketName  bucket名称
     * @param objectName  文件名称
     * @param stream      文件流
     * @param size        大小
     * @param contextType 类型
     * @return PutObjectResult PutObjectResult
     */
    @SuppressWarnings("all")
    public PutObjectResponse putObject(String bucketName, String objectName, InputStream stream, long size,
                                       String contextType) {
        return s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .overrideConfiguration(AwsRequestOverrideConfiguration.builder()
                        .putHeader("Content-Length", String.valueOf(size + 1))
                        .build())
                .build(), RequestBody.fromInputStream(stream, size));
    }

    /**
     * 获取文件信息
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return S3Object
     */
    public GetObjectResponse getObjectInfo(String bucketName, String objectName) {
        return s3Client.getObject(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectName)
                        .build())
                .response();
    }

    /**
     * 删除文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     */
    public void removeObject(String bucketName, String objectName) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .build());
    }


    /**
     * 获取临时凭证
     *
     * @return OssToken
     */
    public OssToken getCredentials() {
        GetSessionTokenRequest request = GetSessionTokenRequest.builder()
                .durationSeconds(ossProperties.getDurationSeconds())
                .build();
        Credentials credentials = stsClient.getSessionToken(request).credentials();
        LocalDateTime localDateTime = DateUtil.toLocalDateTime(credentials.expiration());
        return OssToken.builder()
                .accessKeyId(credentials.accessKeyId())
                .accessKeySecret(credentials.secretAccessKey())
                .expiration(DateUtil.formatLocalDateTime(localDateTime))
                .stsToken(credentials.sessionToken())
                .region(ossProperties.getRegion())
                .build();
    }

    /**
     * 获取临时凭证
     *
     * @param configKey 配置key
     * @return OssToken
     */
    public OssToken getCredentials(String configKey) {
        OssToken credentials = this.getCredentials();
        ResourcePathConfig resourcePathConfig = this.ossProperties.getPathConfig().get(configKey);
        if (resourcePathConfig == null) {
            throw new IllegalArgumentException("configKey is not exist");
        }
        return BeanUtil.copy(resourcePathConfig, credentials);
    }

    @Override
    public void afterPropertiesSet() {
        var credentials = AwsBasicCredentials.create(ossProperties.getAccessKey(),
                ossProperties.getSecretKey());
        var credentialsProvider = StaticCredentialsProvider.create(credentials);
        this.s3Client = S3Client.builder()
                .region(Region.of(ossProperties.getRegion()))
                .endpointOverride(URI.create(ossProperties.getEndpoint()))
                .credentialsProvider(credentialsProvider)
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
        this.s3Presigner = S3Presigner.builder()
                .region(Region.of(ossProperties.getRegion()))
                .endpointOverride(URI.create(ossProperties.getEndpoint()))
                .credentialsProvider(credentialsProvider)
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
        this.stsClient = StsClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.of(ossProperties.getRegion()))
                .endpointOverride(URI.create(ossProperties.getEndpoint()))
                .build();
    }

}
