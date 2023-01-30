package org.shanksit.japedu.admin.dao.repository.impl;

import org.shanksit.japedu.admin.entity.EducationalInstitutionEntity;
import org.shanksit.japedu.admin.dao.mapper.EducationalInstitutionMapper;
import org.shanksit.japedu.admin.dao.repository.IEducationalInstitutionRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class EducationalInstitutionRepository extends ServiceImpl<EducationalInstitutionMapper, EducationalInstitutionEntity> implements IEducationalInstitutionRepository {



}
