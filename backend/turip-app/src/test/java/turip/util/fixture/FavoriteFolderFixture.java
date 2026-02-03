package turip.util.fixture;

import org.springframework.test.util.ReflectionTestUtils;
import turip.favorite.domain.FavoriteFolder;

public class FavoriteFolderFixture {

    public static FavoriteFolder createDefaultFolder() {
        return FavoriteFolder.defaultFolderOf();
    }

    public static FavoriteFolder createDefaultFolderWithId(Long id) {
        FavoriteFolder folder = FavoriteFolder.defaultFolderOf();
        ReflectionTestUtils.setField(folder, "id", id);
        return folder;
    }

    public static FavoriteFolder createCustomFolder(String name) {
        return FavoriteFolder.customFolderOf(name);
    }

    public static FavoriteFolder createCustomFolderWithId(Long id, String name) {
        FavoriteFolder folder = FavoriteFolder.customFolderOf(name);
        ReflectionTestUtils.setField(folder, "id", id);
        return folder;
    }

    public static FavoriteFolder createSharedFolder(String name) {
        FavoriteFolder customFolder = createCustomFolder(name);
        customFolder.shareFolder();
        return customFolder;
    }

    public static FavoriteFolder createSharedFolderWithId(Long id, String name) {
        FavoriteFolder folder = createSharedFolder(name);
        ReflectionTestUtils.setField(folder, "id", id);
        return folder;
    }
}
