//package com.example.holidayswap.service.property.inRoomAmenityTypeService;
//
//import com.example.holidayswap.domain.dto.request.property.inRoomAmenity.InRoomAmenityTypeRequest;
//import com.example.holidayswap.domain.dto.response.property.inRoomAmenity.InRoomAmenityTypeResponse;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//
//import java.util.List;
//
//
//public interface InRoomAmenityTypeService {
//    Page<InRoomAmenityTypeResponse> gets(String name, Pageable pageable);
//
//    List<InRoomAmenityTypeResponse> gets(Long propertyId);
//
//    InRoomAmenityTypeResponse get(Long id);
//
//    InRoomAmenityTypeResponse create(InRoomAmenityTypeRequest inRoomAmenityTypeRequest);
//
//    InRoomAmenityTypeResponse update(Long id, InRoomAmenityTypeRequest inRoomAmenityTypeRequest);
//
//    void delete(Long id);
//
//}
