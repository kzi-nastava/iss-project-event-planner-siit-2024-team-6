package ftn.siit.project.isspoject.service.implementations;

import ftn.siit.project.isspoject.entity.Block;
import ftn.siit.project.isspoject.repository.BlockRepository;
import ftn.siit.project.isspoject.service.interfaces.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlockServiceImpl implements BlockService {

    private final BlockRepository blockRepository;

    @Autowired
    public BlockServiceImpl(BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    @Override
    public boolean existsByBlockerIdAndBlockedId(Integer blockerId, Integer blockedId) {
        return blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId);
    }

    @Override
    public Block save(Block block) {
        return blockRepository.save(block);
    }
}
