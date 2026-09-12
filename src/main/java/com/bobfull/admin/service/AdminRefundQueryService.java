package com.bobfull.admin.service;

import com.bobfull.admin.dto.AdminRefundListItemResponse;
import com.bobfull.common.exception.CommonErrorCode;
import com.bobfull.common.exception.CustomException;
import com.bobfull.common.response.PageResponse;
import com.bobfull.payment.refund.entity.Refund;
import com.bobfull.payment.refund.entity.RefundStatus;
import com.bobfull.payment.refund.repository.RefundRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminRefundQueryService {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));

    private final RefundRepository refundRepository;

    public AdminRefundQueryService(RefundRepository refundRepository) {
        this.refundRepository = refundRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminRefundListItemResponse> getRefunds(String refundStatus, Pageable pageable) {
        RefundStatus status = parseStatus(refundStatus);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), DEFAULT_SORT);
        Page<Refund> refunds = status == null
                ? refundRepository.findAll(sortedPageable)
                : refundRepository.findAllByStatus(status, sortedPageable);
        return PageResponse.from(refunds.map(AdminRefundListItemResponse::from));
    }

    private RefundStatus parseStatus(String refundStatus) {
        if (refundStatus == null || refundStatus.isBlank()) {
            return null;
        }
        try {
            return RefundStatus.valueOf(refundStatus);
        } catch (IllegalArgumentException exception) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
    }
}
