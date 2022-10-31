package ee.mcdimus.matewp.usecase

import org.slf4j.LoggerFactory

interface UseCase<C, R> {

  fun execute(command: C): R

}