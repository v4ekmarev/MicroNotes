package com.develop.core.common.usecase

/**
 * Базовый интерфейс для UseCase без параметров.
 * @param R тип результата
 */
interface UseCase<out R> {
    suspend fun execute(): R
}

/**
 * Базовый интерфейс для UseCase с параметрами.
 * @param P тип параметров
 * @param R тип результата
 */
interface UseCaseWithParams<in P, out R> {
    suspend fun execute(params: P): R
}

/**
 * Базовый интерфейс для UseCase возвращающего Result.
 * @param R тип успешного результата
 */
interface ResultUseCase<out R> {
    suspend fun execute(): Result<R>
}

/**
 * Базовый интерфейс для UseCase с параметрами, возвращающего Result.
 * @param P тип параметров
 * @param R тип успешного результата
 */
interface ResultUseCaseWithParams<in P, out R> {
    suspend fun execute(params: P): Result<R>
}
