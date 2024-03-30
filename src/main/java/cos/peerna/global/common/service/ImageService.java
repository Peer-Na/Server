//package cos.peerna.global.common.service;
//
//import com.amazonaws.SdkClientException;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.CannedAccessControlList;
//import com.amazonaws.services.s3.model.DeleteObjectRequest;
//import com.amazonaws.services.s3.model.ObjectMetadata;
//import com.amazonaws.services.s3.model.PutObjectRequest;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//
//@Slf4j
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class ImageService {
//    private final AmazonS3 amazonS3;
//
//    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
//
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucketName;
//
//    private String generateFileName(Long userId, String originalFileName) {
//        return userId + "-" + simpleDateFormat.format(new Date()) + (originalFileName != null ? "-" + originalFileName : "") ;
//    }
//
//    public String uploadImage(Long userId, MultipartFile image) {
//        Optional<String> imageContentType = Optional.ofNullable(image.getContentType());
//        if (imageContentType.isEmpty() || !imageContentType.get().contains("image"))
//            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported Media Type");
//
//        String fileName = generateFileName(userId, image.getOriginalFilename());
//
//        ObjectMetadata objectMetadata = new ObjectMetadata();
//        objectMetadata.setContentType(imageContentType.get());
//        objectMetadata.setContentLength(image.getSize());
//
//        try (InputStream inputStream = image.getInputStream()) {
//            amazonS3.putObject(
//                    new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata).withCannedAcl(
//                            CannedAccessControlList.PublicRead));
//        } catch (SdkClientException | IOException e) {
//            log.error("File Upload Error", e);
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File Upload Error");
//        }
//        return amazonS3.getUrl(bucketName, fileName).toString();
//    }
//
//    public List<String> uploadImages(Long userId, List<MultipartFile> images) {
//        ArrayList<String> uploadedImages = new ArrayList<>();
//        try {
//            for (MultipartFile image : images) {
//                uploadedImages.add(uploadImage(userId, image));
//            }
//        } catch (ResponseStatusException e) {
//            if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
//                for (String uploadedImage : uploadedImages) {
//                    deleteImage(uploadedImage);
//                }
//                throw e;
//            }
//        }
//        return uploadedImages;
//    }
//
//    public String deleteImage(String url) {
//        String key = url.substring(url.lastIndexOf('/') + 1);
//        try {
//            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
//        } catch (SdkClientException e) {
//            log.error("File Delete Error", e);
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File Delete Error");
//        }
//        return url;
//    }
//
//    public List<String> deleteImages(List<String> urls) {
//        ArrayList<String> deletedImages = new ArrayList<>();
//        for (String url : urls) {
//            try {
//                deletedImages.add(deleteImage(url));
//            }
//            catch (ResponseStatusException ignored) {}
//        }
//        return deletedImages;
//    }
//}
