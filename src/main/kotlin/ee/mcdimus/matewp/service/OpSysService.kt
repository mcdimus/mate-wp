package ee.mcdimus.matewp.service

import java.nio.file.Path

/**
 * @author Dmitri Maksimov
 */
interface OpSysService {

  fun setAsWallpaper(filePath: Path)

  fun getCurrentWallpaper(): Path

}
