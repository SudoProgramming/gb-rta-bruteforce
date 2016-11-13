/***************************************************************************
 *   Copyright (C) 2007 by Sindre Aamås                                    *
 *   sinamas@users.sourceforge.net                                         *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License version 2 as     *
 *   published by the Free Software Foundation.                            *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License version 2 for more details.                *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   version 2 along with this program; if not, write to the               *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
#include "gambatte.h"
#include "cpu.h"
#include "initstate.h"
#include "savestate.h"
#include "state_osd_elements.h"
#include "loadsave.h"
#include "statesaver.h"
#include "file/file.h"
#include <sstream>
#include <cstring>

static const std::string itos(const int i) {
	std::stringstream ss;
	ss << i;
	return ss.str();
}

static const std::string statePath(const std::string &basePath, const int stateNo) {
	return basePath + "_" + itos(stateNo) + ".gqs";
}

namespace gambatte {
	
GB::GB() : p_(new Priv) {}

GB::~GB() {
	//if (p_->cpu.loaded())
	//	p_->cpu.saveSavedata();
	
	delete p_;
}

long GB::runFor(gambatte::uint_least32_t *const videoBuf, const int pitch,
			gambatte::uint_least32_t *const soundBuf, unsigned &samples) {
	if (!p_->cpu.loaded()) {
		samples = 0;
		return -1;
	}
	
	p_->cpu.setVideoBuffer(videoBuf, pitch);
	p_->cpu.setSoundBuffer(soundBuf);
	const long cyclesSinceBlit = p_->cpu.runFor(samples * 2);
	samples = p_->cpu.fillSoundBuffer();
	
	return cyclesSinceBlit < 0 ? cyclesSinceBlit : static_cast<long>(samples) - (cyclesSinceBlit >> 1);
}

void GB::reset(const std::time_t now) {
	if (p_->cpu.loaded()) {
		//p_->cpu.saveSavedata();
		
		SaveState state;
		p_->cpu.setStatePtrs(state);
		setInitState(state, !(p_->loadflags & FORCE_DMG), p_->loadflags & GBA_CGB, now);
		p_->cpu.loadState(state);
		p_->cpu.loadSavedata();
	}
}

void GB::setInputGetter(InputGetter *getInput) {
	p_->cpu.setInputGetter(getInput);
}

void GB::setSaveDir(const std::string &sdir) {
	p_->cpu.setSaveDir(sdir);
}

void GB::setRTCCallback(std::time_t (*callback)()) {
	p_->cpu.setRTCCallback(callback);
}

LoadRes GB::load(std::string const &romfile, const std::time_t now, unsigned const flags) {
	//if (p_->cpu.loaded())
		//p_->cpu.saveSavedata();
	
	LoadRes const loadres = p_->cpu.load(romfile, flags & FORCE_DMG, flags & MULTICART_COMPAT);
	
	if (loadres == LOADRES_OK) {
		SaveState state;
		p_->cpu.setStatePtrs(state);
		p_->loadflags = flags;
		setInitState(state, !(flags & FORCE_DMG), flags & GBA_CGB, now);
		p_->cpu.loadState(state);
		p_->cpu.loadSavedata();
		
		p_->stateNo = 1;
		p_->cpu.setOsdElement(std::auto_ptr<OsdElement>());
	}
	
	return loadres;
}

unsigned int GB::loadGBCBios(std::string const &biosfile) {
	std::auto_ptr<File> const bios(newFileInstance(biosfile));
	char newBiosBuffer[0x900];
	int i, sz;

	if (bios->fail())
		return -1;

	sz = bios->size();
	if (sz != 0x900)
		return -2;

	bios->read(newBiosBuffer, sz);
	if (bios->fail())
		return -1;

	for(i=0x100;i<0x200;i++) {
		if(newBiosBuffer[i] != 0x00) {
			return -3;
		}
	}

	memcpy(p_->cpu.cgbBiosBuffer(), newBiosBuffer, sz);

	return 0;
}

unsigned int GB::loadDMGBios(std::string const &biosfile) {
	std::auto_ptr<File> const bios(newFileInstance(biosfile));
	char newBiosBuffer[0x100];
	int i, sz;

	if (bios->fail())
		return -1;

	sz = bios->size();
	if (sz != 0x100)
		return -2;

	bios->read(newBiosBuffer, sz);
	if (bios->fail())
		return -1;

	memcpy(p_->cpu.dmgBiosBuffer(), newBiosBuffer, sz);

	return 0;
}
/*
unsigned int GB::loadBios(std::string const &biosfile) {
	scoped_ptr<File> const bios(newFileInstance(biosfile));
	char newBiosBuffer[0x900];
	int i;
	if (bios->fail())
		return -1;
	if (bios->size() != 0x900)
		return -2;
	bios->read(newBiosBuffer, 0x900);
	if (bios->fail())
		return -1;
	for(i=0x100;i<0x200;i++) {
	    if(newBiosBuffer[i] != 0x00) {
			return -3;
		}
	}
	memcpy(p_->cpu.getBiosBuffer(), newBiosBuffer, 0x900);
	return 0;
}
*/
bool GB::isCgb() const {
	return p_->cpu.isCgb();
}

bool GB::isLoaded() const {
	return p_->cpu.loaded();
}

void GB::saveSavedata() {
	if (p_->cpu.loaded())
		p_->cpu.saveSavedata();
}

void GB::setDmgPaletteColor(unsigned palNum, unsigned colorNum, unsigned rgb32) {
	p_->cpu.setDmgPaletteColor(palNum, colorNum, rgb32);
}

bool GB::loadState(const std::string &filepath) {
	if (p_->cpu.loaded()) {
		//p_->cpu.saveSavedata();
		
		SaveState state;
		p_->cpu.setStatePtrs(state);
		
		if (StateSaver::loadState(state, filepath, true, p_->cpu.gbIsCgb() ? 1 : 0)) {
			p_->cpu.loadState(state);
			return true;
		}
	}

	return false;
}

bool GB::saveState(const gambatte::uint_least32_t *const videoBuf, const int pitch) {
	if (saveState(videoBuf, pitch, statePath(p_->cpu.saveBasePath(), p_->stateNo))) {
		p_->cpu.setOsdElement(newStateSavedOsdElement(p_->stateNo));
		return true;
	}

	return false;
}

bool GB::loadState() {
	if (loadState(statePath(p_->cpu.saveBasePath(), p_->stateNo))) {
		p_->cpu.setOsdElement(newStateLoadedOsdElement(p_->stateNo));
		return true;
	}

	return false;
}

bool GB::saveState(const gambatte::uint_least32_t *const videoBuf, const int pitch, const std::string &filepath) {
	if (p_->cpu.loaded()) {
		SaveState state;
		p_->cpu.setStatePtrs(state);
		p_->cpu.saveState(state);
		return StateSaver::saveState(state, videoBuf, pitch, filepath);
	}

	return false;
}

void GB::selectState(int n) {
	n -= (n / 10) * 10;
	p_->stateNo = n < 0 ? n + 10 : n;
	
	if (p_->cpu.loaded())
		p_->cpu.setOsdElement(newSaveStateOsdElement(statePath(p_->cpu.saveBasePath(), p_->stateNo), p_->stateNo));
}

int GB::currentState() const { return p_->stateNo; }

std::string const GB::romTitle() const {
	if (p_->cpu.loaded()) {
		char title[0x11];
		std::memcpy(title, p_->cpu.romTitle(), 0x10);
		title[(title[0xF] & 0x80) ? 0xF : 0x10] = '\0';
		return std::string(title);
	}
	
	return std::string();
}

PakInfo const GB::pakInfo() const { return p_->cpu.pakInfo(p_->loadflags & MULTICART_COMPAT); }

void GB::setGameGenie(const std::string &codes) {
	p_->cpu.setGameGenie(codes);
}

void GB::setGameShark(const std::string &codes) {
	p_->cpu.setGameShark(codes);
}

}
