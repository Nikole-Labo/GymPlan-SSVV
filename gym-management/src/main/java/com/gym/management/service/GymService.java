package com.gym.management.service;

import com.gym.management.exception.BusinessException;
import com.gym.management.model.Gym;
import com.gym.management.repository.AbonamentRepository;
import com.gym.management.repository.GymRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GymService {

    private final GymRepository gymRepository;
    private final AbonamentRepository abonamentRepository;

    public GymService(GymRepository gymRepository, AbonamentRepository abonamentRepository) {
        this.gymRepository = gymRepository;
        this.abonamentRepository = abonamentRepository;
    }

    @Transactional(readOnly = true)
    public List<Gym> findAll() {
        return gymRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Gym findById(Long id) {
        return gymRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gym not found: " + id));
    }

    public Gym save(Gym gym) {
        return gymRepository.save(gym);
    }

    public void delete(Long id) {
        Gym gym = findById(id);
        long subscriptionCount = abonamentRepository.countByGymId(id);
        if (subscriptionCount > 0) {
            throw new BusinessException(buildDeleteBlockedMessage(gym.getName(), subscriptionCount));
        }
        gymRepository.deleteById(id);
    }

    private static String buildDeleteBlockedMessage(String gymName, long count) {
        String noun = count == 1 ? "subscription" : "subscriptions";
        return "Cannot delete gym \"" + gymName + "\" because it has "
                + count + " linked " + noun + ". "
                + "Delete those subscriptions first from the Subscriptions page.";
    }
}
