package com.soloist.player.exception

import com.soloist.player.model.entity.Model
import kotlin.reflect.KClass

class ModelNotFoundException(klass: KClass<out Model>, id: Any) :
	RuntimeException("${klass.simpleName} not found id=$id")
