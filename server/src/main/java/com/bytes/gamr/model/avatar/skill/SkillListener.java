package com.bytes.gamr.model.avatar.skill;

import com.bytes.gamr.model.avatar.Avatar;

public interface SkillListener<T extends Avatar> {
	
	void activated(Skill<T> skill, Avatar source,  Avatar target);

}
