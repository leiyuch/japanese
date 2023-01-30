package org.shanksit.japedu.admin.dao.repository.impl;

import org.shanksit.japedu.admin.entity.TranscriptEntity;
import org.shanksit.japedu.admin.dao.mapper.TranscriptMapper;
import org.shanksit.japedu.admin.dao.repository.ITranscriptRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class TranscriptRepository extends ServiceImpl<TranscriptMapper, TranscriptEntity> implements ITranscriptRepository {



}
