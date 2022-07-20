package com.zachtib.ksp

@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
annotation class Screen

@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class ScreenKey

@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class AcceptsScreenKey
