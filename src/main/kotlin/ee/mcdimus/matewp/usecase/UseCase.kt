package ee.mcdimus.matewp.usecase

interface UseCase<C, R> {

  fun execute(command: C): R

}
