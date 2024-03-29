package com.example.holidayswap.service.resort;

import com.example.holidayswap.domain.dto.request.resort.ResortRequest;
import com.example.holidayswap.domain.dto.request.resort.ResortUpdateRequest;
import com.example.holidayswap.domain.dto.response.resort.ResortResponse;
import com.example.holidayswap.domain.entity.property.PropertyType;
import com.example.holidayswap.domain.entity.resort.Resort;
import com.example.holidayswap.domain.entity.resort.ResortStatus;
import com.example.holidayswap.domain.entity.resort.amentity.ResortAmenity;
import com.example.holidayswap.domain.exception.DataIntegrityViolationException;
import com.example.holidayswap.domain.exception.DuplicateRecordException;
import com.example.holidayswap.domain.exception.EntityNotFoundException;
import com.example.holidayswap.domain.mapper.address.CountryMapper;
import com.example.holidayswap.domain.mapper.address.DistrictMapper;
import com.example.holidayswap.domain.mapper.address.LocationMapper;
import com.example.holidayswap.domain.mapper.address.StateOrProvinceMapper;
import com.example.holidayswap.domain.mapper.resort.ResortMapper;
import com.example.holidayswap.repository.address.CountryRepository;
import com.example.holidayswap.repository.address.DistrictRepository;
import com.example.holidayswap.repository.address.StateOrProvinceRepository;
import com.example.holidayswap.repository.property.PropertyTypeRespository;
import com.example.holidayswap.repository.resort.ResortRepository;
import com.example.holidayswap.repository.resort.amenity.ResortAmenityRepository;
import com.example.holidayswap.service.address.LocationService;
import com.example.holidayswap.service.booking.IBookingService;
import com.example.holidayswap.service.resort.amenity.ResortAmenityTypeService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.holidayswap.constants.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class ResortServiceImpl implements ResortService {
    private final ResortRepository resortRepository;
    private final ResortImageService resortImageService;
    private final ResortAmenityTypeService resortAmenityTypeService;
    private final ResortAmenityRepository resortAmenityRepository;
    private final PropertyTypeRespository propertyTypeRespository;
    private final LocationService locationService;
    private final DistrictRepository districtRepository;
    private final StateOrProvinceRepository stateOrProvinceRepository;
    private final CountryRepository countryRepository;
    private final IBookingService bookingService;
    private final ResortMapper resortMapper;
    private final IResortMaintanceService resortMaintanceService;


    @Override
    public Page<ResortResponse> gets(String locationName, String name, Set<Long> listOfResortAmenity, ResortStatus resortStatus, Pageable pageable) {
        Page<Resort> entities = null;
        entities = resortRepository.findAllByFilter(StringUtils.stripAccents(locationName).toUpperCase(), name, listOfResortAmenity, resortStatus, pageable);
        return entities.map(e -> {
            var dto = ResortMapper.INSTANCE.toResortResponse(e);
            dto.setResortImages(resortImageService.gets(e.getId()));
//            dto.setResortAmenityTypes(resortAmenityTypeService.gets(e.getId()));
            return dto;
        });
    }

    @Override
    public List<ResortResponse> getsListResortHaveProperty() {
        var list = resortRepository.getsListResortHaveProperty();
        return list.stream().map(resortMapper::toResortResponse).collect(Collectors.toList());
    }

    @Override
    public ResortResponse get(Long id) {
        var entity = resortRepository.findByIdAndIsDeletedIsFalse(id).orElseThrow(() -> new EntityNotFoundException(RESORT_NOT_FOUND));
        var dtoResponse = ResortMapper.INSTANCE.toResortResponse(entity);
//        dtoResponse.setResortMaintances(resortMaintanceService.getResortMaintanceByResortId(id));

        dtoResponse.setResortImages(resortImageService.gets(id));
        dtoResponse.setResortAmenityTypes(resortAmenityTypeService.gets(entity.getId()));

        return dtoResponse;
    }

    @Override
    @Transactional
    public ResortResponse create(ResortRequest resortRequest) {
        if (resortRepository.findByResortNameEqualsIgnoreCaseAndIsDeletedFalse(resortRequest.getResortName()).isPresent())
            throw new DuplicateRecordException(DUPLICATE_RESORT_NAME);
        var entity = ResortMapper.INSTANCE.toResort(resortRequest);
        entity.setStatus(ResortStatus.ACTIVE);
        List<ResortAmenity> resortAmenities = new ArrayList<>();
        resortRequest.getAmenities().forEach(e -> {
            resortAmenities.add(resortAmenityRepository.findByIdAndIsDeletedFalse(e).orElseThrow(() -> new EntityNotFoundException(RESORT_AMENITY_NOT_FOUND)));
        });
        List<PropertyType> propertyTypes = new ArrayList<>();
        resortRequest.getPropertyTypes().forEach(e -> {
            propertyTypes.add(propertyTypeRespository.findByIdAndIsDeletedFalse(e).orElseThrow(() -> new EntityNotFoundException(PROPERTY_TYPE_NOT_FOUND)));
        });
        entity.setAmenities(resortAmenities);
        entity.setPropertyTypes(propertyTypes);
        LocationMapper.INSTANCE.updateLocation(entity, LocationMapper.INSTANCE.toResort(resortRequest.getLocation()));
        districtRepository.findByCodeEquals(resortRequest.getLocation().getDistrict().getCode())
                .ifPresentOrElse(
                        entity::setDistrict,
                        () -> entity.setDistrict(DistrictMapper.INSTANCE.toDistrict(resortRequest.getLocation().getDistrict())));
        stateOrProvinceRepository.findByCodeEquals(resortRequest.getLocation().getStateOrProvince().getCode())
                .ifPresentOrElse(
                        entity.getDistrict()::setStateProvince,
                        () -> entity.getDistrict()
                                .setStateProvince(
                                        StateOrProvinceMapper.INSTANCE.toStateOrProvince(resortRequest.getLocation().getStateOrProvince())));
        countryRepository.findByCodeEquals(resortRequest.getLocation().getCountry().getCode())
                .ifPresentOrElse(
                        entity.getDistrict().getStateProvince()::setCountry,
                        () -> entity.getDistrict().getStateProvince()
                                .setCountry(CountryMapper.INSTANCE.toCountry(resortRequest.getLocation().getCountry()))
                );
        return ResortMapper.INSTANCE.toResortResponse(resortRepository.save(entity));
    }

    @Override
    @Transactional
    public ResortResponse create(ResortRequest resortRequest, List<MultipartFile> resortImage) {
        var entity = create(resortRequest);
        if (resortImage.size() < 5)
            throw new DataIntegrityViolationException("Please input more than 5 file image of resort");
        if (resortRequest.getAmenities().isEmpty())
            throw new DataIntegrityViolationException("Please input resort amenity");
        if (resortRequest.getPropertyTypes().isEmpty())
            throw new DataIntegrityViolationException("Please input resort's property Type");
        resortImage.forEach(e -> resortImageService.create(entity.getId(), e));
        return get(entity.getId());
    }

    @Override
    public void update(Long id, ResortUpdateRequest resortRequest, List<MultipartFile> resortImage) {
        if (resortRequest.getAmenities().isEmpty())
            throw new DataIntegrityViolationException("Please input resort amenity");
        if (resortRequest.getPropertyTypes().isEmpty())
            throw new DataIntegrityViolationException("Please input resort's property Type");
        var entityFound = resortRepository.findById(id);
        if (entityFound.isPresent() && !Objects.equals(entityFound.get().getId(), id)) {
            throw new DuplicateRecordException(DUPLICATE_RESORT_NAME);
        }
        //resort image required
        int resortCreateMore = 0;
        if (resortImage != null) resortCreateMore = resortImage.size();
        if (resortRequest.getOldImages().size() + resortCreateMore < 5) {
            throw new DataIntegrityViolationException("Resort image must have 5 or more image");
        }

        if (resortRequest.getPropertyTypes().isEmpty() || resortRequest.getPropertyTypes() == null)
            throw new EntityNotFoundException("Property type is required");
        if (resortRequest.getAmenities().isEmpty() || resortRequest.getAmenities() == null)
            throw new EntityNotFoundException("Amenity is required");

        var entity = resortRepository.findByIdAndDeletedFalseAndResortStatus(id, ResortStatus.ACTIVE).orElseThrow(() -> new EntityNotFoundException(RESORT_NOT_FOUND));
        List<ResortAmenity> resortAmenities = new ArrayList<>();
        resortRequest.getAmenities().forEach(e -> {
            resortAmenities.add(resortAmenityRepository.findByIdAndIsDeletedFalse(e).orElseThrow(() -> new EntityNotFoundException(RESORT_AMENITY_NOT_FOUND)));
        });

        entity.getResortImages().forEach(e -> {
            resortImageService.delete(e.getId());
        });

        for (PropertyType propertyType : entity.getPropertyTypes()) {
            propertyType.getResorts().remove(entity);
            propertyTypeRespository.save(propertyType);
        }

        resortImageService.setImageToResort(entity.getId(), resortRequest.getOldImages());
        if (resortImage != null) {
            resortImage.forEach(e -> resortImageService.create(entity.getId(), e));
        }
        List<PropertyType> propertyTypes = new ArrayList<>();
        resortRequest.getPropertyTypes().forEach(e -> {
            propertyTypes.add(propertyTypeRespository.findByIdAndIsDeletedFalse(e).orElseThrow(() -> new EntityNotFoundException(PROPERTY_TYPE_NOT_FOUND)));
        });
        ResortMapper.INSTANCE.updateEntityFromDTO(resortRequest, entity);
        entity.setResortImages(null);
        entity.setAmenities(resortAmenities);
        entity.setPropertyTypes(propertyTypes);
        resortRepository.save(entity);
        locationService.updateLocation(id, resortRequest.getLocation());
    }

    @Override
    public void update(Long id, ResortStatus resortStatus) {
        var entity = resortRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new EntityNotFoundException(RESORT_NOT_FOUND));
        if (resortStatus == ResortStatus.DEACTIVATE) {
//            bookingService.deactiveResortNotifyBookingUser(id, LocalDate.now());
        }
        entity.setStatus(resortStatus);
        Long i = resortRepository.save(entity).getId();
    }

    @Override
    public void delete(Long id, LocalDate startDate ) {
        var inRoomAmenityTypeFound = resortRepository.findByIdAndDeletedFalseAndResortStatus(id, ResortStatus.ACTIVE).orElseThrow(() -> new EntityNotFoundException(RESORT_NOT_FOUND));
//        bookingService.deactiveResortNotifyBookingUser(id, startDate);
        inRoomAmenityTypeFound.setDeleted(true);
        resortRepository.save(inRoomAmenityTypeFound);
    }

    @Transactional
    @Override
    public void updateStatus(Long id, ResortStatus resortStatus, LocalDateTime startDate, LocalDateTime endDate, List<MultipartFile> resortImage) throws MessagingException, IOException {
        if(startDate.isBefore(LocalDateTime.now())) throw new DataIntegrityViolationException("Start date must be after today");
        if(startDate.isEqual(LocalDateTime.now())) throw new DataIntegrityViolationException("Start date must be after today");
        var entity = resortRepository.findByIdAndDeletedFalseAndResortStatus(id, ResortStatus.ACTIVE).orElseThrow(() -> new EntityNotFoundException("Resort not available now"));
        List<String> listImage = resortMaintanceService.CreateResortMaintance(id, startDate, endDate, resortStatus, resortImage);
 // TODO: create issue and notification
        bookingService.deactiveResortNotifyBookingUser(id, startDate, endDate, resortStatus, listImage);

//        resortRepository.save(entity);
    }
}
