package com.pmk.freeplayer.feature.player.data.player

import com.pmk.freeplayer.feature.player.domain.model.QueueItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QueueManager @Inject constructor() {
	
	private val _queue = mutableListOf<QueueItem>()
	private var _shuffledIndices: List<Int> = emptyList()
	private var _currentIndex: Int = 0
	var shuffleEnabled: Boolean = false
		private set
	
	val queue: List<QueueItem> get() = _queue.toList()
	val currentIndex: Int      get() = _currentIndex
	
	val currentItem: QueueItem?
		get() = _queue.getOrNull(_currentIndex)
	
	val hasNext: Boolean
		get() = _currentIndex < _queue.lastIndex
	
	val hasPrevious: Boolean
		get() = _currentIndex > 0
	
	fun set(items: List<QueueItem>, startIndex: Int) {
		_queue.clear()
		_queue.addAll(items)
		_currentIndex = startIndex.coerceIn(0, items.lastIndex)
		if (shuffleEnabled) buildShuffledIndices()
	}
	
	fun add(item: QueueItem) { _queue.add(item) }
	
	fun remove(index: Int) {
		if (index !in _queue.indices) return
		_queue.removeAt(index)
		if (index < _currentIndex) _currentIndex--
		_currentIndex = _currentIndex.coerceIn(0, (_queue.lastIndex).coerceAtLeast(0))
	}
	
	fun move(from: Int, to: Int) {
		if (from !in _queue.indices || to !in _queue.indices) return
		val item = _queue.removeAt(from)
		_queue.add(to, item)
		_currentIndex = when (_currentIndex) {
			from -> to
			in (minOf(from, to)..maxOf(from, to)) ->
				if (from < to) _currentIndex - 1 else _currentIndex + 1
			else -> _currentIndex
		}
	}
	
	fun clear() {
		_queue.clear()
		_currentIndex = 0
		_shuffledIndices = emptyList()
	}
	
	fun advanceToNext(): Boolean {
		val nextIndex = if (shuffleEnabled) nextShuffledIndex() else _currentIndex + 1
		return if (nextIndex <= _queue.lastIndex) {
			_currentIndex = nextIndex
			true
		} else false
	}
	
	fun backToPrevious(): Boolean {
		val prevIndex = if (shuffleEnabled) previousShuffledIndex() else _currentIndex - 1
		return if (prevIndex >= 0) {
			_currentIndex = prevIndex
			true
		} else false
	}
	
	fun jumpTo(index: Int) {
		_currentIndex = index.coerceIn(0, _queue.lastIndex)
	}
	
	fun setShuffle(enabled: Boolean) {
		shuffleEnabled = enabled
		if (enabled) buildShuffledIndices() else _shuffledIndices = emptyList()
	}
	
	private fun buildShuffledIndices() {
		_shuffledIndices = (_queue.indices - _currentIndex)
			.shuffled()
			.toMutableList()
			.also { it.add(0, _currentIndex) }
	}
	
	private fun nextShuffledIndex(): Int {
		val pos = _shuffledIndices.indexOf(_currentIndex)
		return if (pos < _shuffledIndices.lastIndex) _shuffledIndices[pos + 1] else Int.MAX_VALUE
	}
	
	private fun previousShuffledIndex(): Int {
		val pos = _shuffledIndices.indexOf(_currentIndex)
		return if (pos > 0) _shuffledIndices[pos - 1] else -1
	}
}