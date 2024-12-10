package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.entity.Block;
import ftn.siit.project.isspoject.entity.User;

public interface BlockService {
    boolean existsByBlockerIdAndBlockedId(User blockerId, User blockedId);

    Block save(Block block);
}
