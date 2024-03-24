package eu.maksimov.matewp.core.usecase

interface UseCase<C, R> {

  fun execute(command: C): R

}
