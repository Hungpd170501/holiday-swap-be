package com.example.holidayswap.service.property.amenity;

import com.example.holidayswap.domain.dto.request.property.amenity.InRoomAmenityTypeRequest;
import com.example.holidayswap.domain.dto.response.property.amenity.InRoomAmenityTypeResponse;
import com.example.holidayswap.domain.exception.DuplicateRecordException;
import com.example.holidayswap.domain.exception.EntityNotFoundException;
import com.example.holidayswap.domain.mapper.property.amenity.InRoomAmenityTypeMapper;
import com.example.holidayswap.repository.property.amenity.InRoomAmenityTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.example.holidayswap.constants.ErrorMessage.DUPLICATE_IN_ROOM_AMENITY_TYPE;
import static com.example.holidayswap.constants.ErrorMessage.IN_ROOM_AMENITY_TYPE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class InRoomAmenityTypeServiceImpl implements InRoomAmenityTypeService {
    private final InRoomAmenityTypeRepository inRoomAmenityTypeRepository;
    private final InRoomAmenityService inRoomAmenityService;

    @Override
    public Page<InRoomAmenityTypeResponse> gets(String name, Pageable pageable) {
        var dtoResponse =
                inRoomAmenityTypeRepository.findAllByInRoomAmenityTypeNameContainingIgnoreCaseAndIsDeletedFalse(name, pageable).
                        map(InRoomAmenityTypeMapper.INSTANCE::toDtoResponse);
        dtoResponse.forEach(e -> {
            e.setInRoomAmenities(inRoomAmenityService.gets(e.getId()));
        });
        return dtoResponse;
    }

    @Override
    public List<InRoomAmenityTypeResponse> gets(Long propertyId) {
        var dto = inRoomAmenityTypeRepository.findAllByPropertyId(propertyId).stream().map(
                InRoomAmenityTypeMapper.INSTANCE::toDtoResponse).toList();
        dto.forEach(e -> {
            var inRoomAmenity = inRoomAmenityService.gets(propertyId, e.getId());
            e.setInRoomAmenities(inRoomAmenity);
        });
        return dto;
    }

    @Override
    public List<InRoomAmenityTypeResponse> gets() {
        var dto = inRoomAmenityTypeRepository.findAll().stream().map(
                InRoomAmenityTypeMapper.INSTANCE::toDtoResponse).toList();
        dto.forEach(e -> {
            var inRoomAmenity = inRoomAmenityService.getByInRoomAmenityType(e.getId());
            e.setInRoomAmenities(inRoomAmenity);
        });
        return dto;
    }

    @Override
    public InRoomAmenityTypeResponse get(Long id) {
        var dto = InRoomAmenityTypeMapper.INSTANCE.toDtoResponse(inRoomAmenityTypeRepository.findByInRoomAmenityTypeIdAndIsDeletedFalse(id).
                orElseThrow(() -> new EntityNotFoundException(IN_ROOM_AMENITY_TYPE_NOT_FOUND)));
        dto.setInRoomAmenities(inRoomAmenityService.gets(dto.getId()));
        return dto;
    }

    @Override
    public InRoomAmenityTypeResponse create(InRoomAmenityTypeRequest dtoRequest) {
        if (inRoomAmenityTypeRepository.
                findByInRoomAmenityTypeNameEqualsIgnoreCaseAndIsDeletedFalse(dtoRequest.getInRoomAmenityTypeName()).
                isPresent()) throw new DuplicateRecordException(DUPLICATE_IN_ROOM_AMENITY_TYPE);
        return InRoomAmenityTypeMapper.INSTANCE.toDtoResponse(inRoomAmenityTypeRepository.save(InRoomAmenityTypeMapper.INSTANCE.toEntity(dtoRequest)));
    }

    @Override
    public InRoomAmenityTypeResponse update(Long id, InRoomAmenityTypeRequest dtoRequest) {
        var entity = inRoomAmenityTypeRepository.
                findByInRoomAmenityTypeIdAndIsDeletedFalse(id).orElseThrow(
                        () -> new EntityNotFoundException(IN_ROOM_AMENITY_TYPE_NOT_FOUND));
        var entityFound = inRoomAmenityTypeRepository.
                findByInRoomAmenityTypeNameEqualsIgnoreCaseAndIsDeletedFalse(dtoRequest.getInRoomAmenityTypeName());
        if (entityFound.isPresent() && !Objects.equals(entityFound.get().getId(), id))
            throw new DuplicateRecordException(DUPLICATE_IN_ROOM_AMENITY_TYPE);
        InRoomAmenityTypeMapper.INSTANCE.updateEntityFromDTO(dtoRequest, entity);
        return InRoomAmenityTypeMapper.INSTANCE.toDtoResponse(inRoomAmenityTypeRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        var entity = inRoomAmenityTypeRepository.findByInRoomAmenityTypeIdAndIsDeletedFalse(id).orElseThrow(() -> new EntityNotFoundException(IN_ROOM_AMENITY_TYPE_NOT_FOUND));
        entity.setIsDeleted(true);
        inRoomAmenityTypeRepository.save(entity);
    }
}
