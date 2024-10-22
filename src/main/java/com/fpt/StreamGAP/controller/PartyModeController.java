package com.fpt.StreamGAP.controller;

import com.fpt.StreamGAP.dto.PartyModeDTO;
import com.fpt.StreamGAP.entity.PartyMode;
import com.fpt.StreamGAP.service.PartyModeService;
import org.springframework.beans.factory.annotation.Autowired;
import com.fpt.StreamGAP.dto.ReqRes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/partymodes")
public class PartyModeController {

    @Autowired
    private PartyModeService partyModeService;

    @GetMapping
    public ReqRes getAllPartyModes() {
        List<PartyMode> partyModes = partyModeService.getAllPartyModesForCurrentUser();
        List<PartyModeDTO> partyModeDTOs = partyModes.stream()
                .map(partyMode -> {
                    PartyModeDTO dto = new PartyModeDTO();
                    dto.setParty_Id(partyMode.getPartyId()); // Sử dụng party_id
                    dto.setParty_Name(partyMode.getParty_name());
                    dto.setHost_Id(partyMode.getHost().getUser_id());
                    dto.setCreated_at(partyMode.getCreated_at());
                    return dto;
                })
                .collect(Collectors.toList());

        ReqRes response = new ReqRes();
        response.setStatusCode(200);
        response.setMessage("Party modes retrieved successfully");
        response.setPartyModeList(partyModeDTOs);

        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReqRes> getPartyModeById(@PathVariable Integer id) {
        return partyModeService.getPartyModeByIdForCurrentUser(id)
                .map(partyMode -> {
                    PartyModeDTO dto = new PartyModeDTO();
                    dto.setParty_Id(partyMode.getPartyId());
                    dto.setParty_Name(partyMode.getParty_name());
                    dto.setHost_Id(partyMode.getHost().getUser_id());
                    dto.setCreated_at(partyMode.getCreated_at());

                    ReqRes response = new ReqRes();
                    response.setStatusCode(200);
                    response.setMessage("Party mode retrieved successfully");
                    response.setPartyModeList(List.of(dto));

                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    ReqRes response = new ReqRes();
                    response.setStatusCode(404);
                    response.setMessage("Party mode not found");
                    return ResponseEntity.status(404).body(response);
                });
    }

    @PostMapping
    public ResponseEntity<ReqRes> createPartyMode(@RequestBody PartyModeDTO partyModeDTO) {
        try {
            PartyMode partyMode = new PartyMode();
            partyMode.setParty_name(partyModeDTO.getParty_Name());

            PartyMode savedPartyMode = partyModeService.savePartyMode(partyMode);

            PartyModeDTO dto = new PartyModeDTO();
            dto.setParty_Id(savedPartyMode.getPartyId());
            dto.setParty_Name(savedPartyMode.getParty_name());
            dto.setHost_Id(savedPartyMode.getHost().getUser_id());
            dto.setCreated_at(savedPartyMode.getCreated_at());

            ReqRes response = new ReqRes();
            response.setStatusCode(201);
            response.setMessage("Party mode created successfully");
            response.setPartyModeList(List.of(dto));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            // Xử lý ngoại lệ và trả về lỗi
            ReqRes response = new ReqRes();
            response.setStatusCode(500);
            response.setMessage("Failed to create party mode: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReqRes> updatePartyMode(@PathVariable Integer id, @RequestBody PartyModeDTO partyModeDTO) {
        try {
            // Lấy đối tượng PartyMode từ DB dựa trên ID
            Optional<PartyMode> existingPartyMode = partyModeService.getPartyModeByIdForCurrentUser(id);

            if (existingPartyMode.isPresent()) {
                // Cập nhật các giá trị mới
                PartyMode partyMode = existingPartyMode.get();
                partyMode.setParty_name(partyModeDTO.getParty_Name());
                // Các trường khác cũng có thể được cập nhật ở đây nếu cần

                PartyMode updatedPartyMode = partyModeService.savePartyMode(partyMode);

                // Chuẩn bị response
                PartyModeDTO dto = new PartyModeDTO();
                dto.setParty_Id(updatedPartyMode.getPartyId());
                dto.setParty_Name(updatedPartyMode.getParty_name());
                dto.setHost_Id(updatedPartyMode.getHost().getUser_id());
                dto.setCreated_at(updatedPartyMode.getCreated_at());

                ReqRes response = new ReqRes();
                response.setStatusCode(200);
                response.setMessage("Party mode updated successfully");
                response.setPartyModeList(List.of(dto));

                return ResponseEntity.ok(response);
            } else {
                ReqRes response = new ReqRes();
                response.setStatusCode(404);
                response.setMessage("Party mode not found");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            ReqRes response = new ReqRes();
            response.setStatusCode(500);
            response.setMessage("Failed to update party mode: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ReqRes> deletePartyMode(@PathVariable Integer id) {
        if (partyModeService.getPartyModeByIdForCurrentUser(id).isPresent()) {
            partyModeService.deletePartyModeForCurrentUser(id);
            ReqRes response = new ReqRes();
            response.setStatusCode(204);
            response.setMessage("Party mode deleted successfully");
            return ResponseEntity.noContent().build();
        } else {
            ReqRes response = new ReqRes();
            response.setStatusCode(404);
            response.setMessage("Party mode not found");
            return ResponseEntity.status(404).body(response);
        }
    }
}
