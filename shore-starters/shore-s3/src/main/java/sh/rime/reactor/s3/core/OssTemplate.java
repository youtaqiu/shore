package sh.rime.reactor.s3.core;

import cn.hutool.core.date.DateUtil;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;
import sh.rime.reactor.core.util.BeanUtil;
import sh.rime.reactor.s3.bean.OssToken;
import sh.rime.reactor.s3.bean.ResourcePathConfig;
import sh.rime.reactor.s3.props.OssProperties;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * oss 模板
 *
 * @author youta
 **/
@SuppressWarnings("unused")
public class OssTemplate implements InitializingBean {

    private final OssProperties ossProperties;

    private AmazonS3 amazonS3;
    private AWSSecurityTokenService awsSecurityTokenService;

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
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            amazonS3.createBucket((bucketName));
        }
    }

    /**
     * 获取全部bucket
     *
     * @return bucket列表
     */
    public List<Bucket> getAllBuckets() {
         ListBucketsPaginatedRequest request = new ListBucketsPaginatedRequest();
        return amazonS3.listBuckets(request).getBuckets();
    }

    /**
     * get bucket
     *
     * @param bucketName bucket名称
     * @return bucket
     */
    public Optional<Bucket> getBucket(String bucketName) {
        return this.getAllBuckets().stream().filter(b -> b.getName().equals(bucketName)).findFirst();
    }

    /**
     * remove bucket
     *
     * @param bucketName bucket名称
     */
    public void removeBucket(String bucketName) {
        amazonS3.deleteBucket(bucketName);
    }

    /**
     * 根据文件前置查询文件
     *
     * @param bucketName bucket名称
     * @param prefix     前缀
     * @return 文件列表
     */
    public List<S3ObjectSummary> getAllObjectsByPrefix(String bucketName, String prefix) {
        ObjectListing objectListing = amazonS3.listObjects(bucketName, prefix);
        return new ArrayList<>(objectListing.getObjectSummaries());
    }

    /**
     * 获取文件外链，只用于下载
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param minutes    过期时间，单位分钟,请注意该值必须小于7天
     * @return url
     */
    public String getObjectURL(String bucketName, String objectName, int minutes) {
        return getObjectURL(bucketName, objectName, Duration.ofMinutes(minutes));
    }

    /**
     * 获取文件外链，只用于下载
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param expires    过期时间,请注意该值必须小于7天
     * @return url
     */
    public String getObjectURL(String bucketName, String objectName, Duration expires) {
        return getObjectURL(bucketName, objectName, expires, HttpMethod.GET);
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
        return getObjectURL(bucketName, objectName, expires, HttpMethod.PUT);
    }

    /**
     * 获取文件外链
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param minutes    过期时间，单位分钟,请注意该值必须小于7天
     * @param method     文件操作方法：GET（下载）、PUT（上传）
     * @return url
     * HttpMethod method)
     */
    public String getObjectURL(String bucketName, String objectName, int minutes, HttpMethod method) {
        return getObjectURL(bucketName, objectName, Duration.ofMinutes(minutes), method);
    }

    /**
     * 获取文件外链
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param expires    过期时间，请注意该值必须小于7天
     * @param method     文件操作方法：GET（下载）、PUT（上传）
     * @return url
     * HttpMethod method)
     */
    public String getObjectURL(String bucketName, String objectName, Duration expires, HttpMethod method) {
        // Set the pre-signed URL to expire after `expires`.
        Date expiration = Date.from(Instant.now().plus(expires));

        // Generate the pre-signed URL.
        URL url = amazonS3.generatePresignedUrl(
                new GeneratePresignedUrlRequest(bucketName, objectName).withMethod(method).withExpiration(expiration));
        return url.toString();
    }

    /**
     * 获取文件URL
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return url
     */
    public String getObjectURL(String bucketName, String objectName) {
        URL url = amazonS3.getUrl(bucketName, objectName);
        return url.toString();
    }

    /**
     * 获取文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return 二进制流
     */
    public S3Object getObject(String bucketName, String objectName) {
        return amazonS3.getObject(bucketName, objectName);
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
    public PutObjectResult putObject(String bucketName, String objectName, InputStream stream, long size,
                                     String contextType) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(size);
        objectMetadata.setContentType(contextType);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, stream, objectMetadata);
        // Setting the read limit value to one byte greater than the size of stream will
        // reliably avoid a ResetException
        putObjectRequest.getRequestClientOptions().setReadLimit((int) size + 1);
        return amazonS3.putObject(putObjectRequest);

    }

    /**
     * 获取文件信息
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return S3Object
     */
    public S3Object getObjectInfo(String bucketName, String objectName) {
        return amazonS3.getObject(bucketName, objectName);
    }

    /**
     * 删除文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     */
    public void removeObject(String bucketName, String objectName) {
        amazonS3.deleteObject(bucketName, objectName);
    }


    /**
     * 获取临时凭证
     *
     * @return OssToken
     */
    public OssToken getCredentials() {
        Credentials credentials = awsSecurityTokenService.getSessionToken(new GetSessionTokenRequest()
                        .withDurationSeconds(ossProperties.getDurationSeconds()))
                .getCredentials();
        return OssToken.builder()
                .accessKeyId(credentials.getAccessKeyId())
                .accessKeySecret(credentials.getSecretAccessKey())
                .expiration(DateUtil.formatDateTime(credentials.getExpiration()))
                .stsToken(credentials.getSessionToken())
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
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        AWSCredentials awsCredentials = new BasicAWSCredentials(ossProperties.getAccessKey(),
                ossProperties.getSecretKey());
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(ossProperties.getEndpoint(),
                        ossProperties.getRegion()))
                .disableChunkedEncoding()
                .withPathStyleAccessEnabled(ossProperties.getPathStyleAccess())
                .build();

        this.awsSecurityTokenService = AWSSecurityTokenServiceClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(ossProperties.getAccessKey(), ossProperties.getSecretKey())))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(ossProperties.getEndpoint(),
                        ossProperties.getRegion()))
                .build();
    }

}
