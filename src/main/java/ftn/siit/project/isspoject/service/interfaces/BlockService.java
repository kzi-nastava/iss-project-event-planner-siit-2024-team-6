package ftn.siit.project.isspoject.service.interfaces;

import ftn.siit.project.isspoject.entity.Block;

public interface BlockService {
    boolean existsByBlockerIdAndBlockedId(Integer blockerId, Integer blockedId);

    Block save(Block block);
}
