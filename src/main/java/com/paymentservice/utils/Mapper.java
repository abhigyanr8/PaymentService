package com.paymentservice.utils;


public interface Mapper<D, E> {
    E toEntity(D dto);

    D toDto(E e);

}
