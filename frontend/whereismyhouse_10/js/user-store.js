/**
 * 로컬스토리지 기반 회원 관리 모듈
 * CRUD 기능: Create(회원가입), Read(로그인/조회), Update(정보수정), Delete(회원탈퇴)
 */

const USERS_STORAGE_KEY = 'ssafy-home-users';
const CURRENT_USER_KEY = 'ssafy-home-current-user';

const UserStore = {
  /**
   * 모든 회원 목록 조회
   * @returns {Array} 회원 배열
   */
  loadAll() {
    try {
      const raw = localStorage.getItem(USERS_STORAGE_KEY);
      return raw ? JSON.parse(raw) : [];
    } catch (error) {
      console.error('회원 목록 로드 실패:', error);
      return [];
    }
  },

  /**
   * 회원 목록 저장
   * @param {Array} users - 회원 배열
   */
  saveAll(users) {
    try {
      localStorage.setItem(USERS_STORAGE_KEY, JSON.stringify(users));
    } catch (error) {
      console.error('회원 목록 저장 실패:', error);
    }
  },

  /**
   * 아이디로 회원 조회
   * @param {string} userId - 아이디
   * @returns {Object|null} 회원 객체 또는 null
   */
  findByUserId(userId) {
    const users = this.loadAll();
    return users.find(u => u.userId === userId) || null;
  },

  /**
   * 아이디 중복 확인
   * @param {string} userId - 확인할 아이디
   * @returns {boolean} 중복이면 true, 사용 가능하면 false
   */
  isUserIdTaken(userId) {
    return this.findByUserId(userId) !== null;
  },

  /**
   * 회원가입 (Create)
   * @param {Object} userData - 회원 데이터 { userId, password, name, email, phone }
   * @returns {Object} { success: boolean, message: string, user?: Object }
   */
  register(userData) {
    const { userId, password, name, email, phone } = userData;

    // 유효성 검사
    if (!userId || !password || !name || !email || !phone) {
      return { success: false, message: '모든 필드를 입력해주세요.' };
    }

    // 아이디 중복 확인
    if (this.isUserIdTaken(userId)) {
      return { success: false, message: '이미 사용 중인 아이디입니다.' };
    }

    // 새 회원 생성
    const newUser = {
      userId,
      password, // 실제 서비스에서는 해시 처리 필요
      name,
      email,
      phone,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };

    // 회원 목록에 추가
    const users = this.loadAll();
    users.push(newUser);
    this.saveAll(users);

    // 비밀번호를 제외한 정보 반환
    const { password: _, ...userWithoutPassword } = newUser;
    return { 
      success: true, 
      message: '회원가입이 완료되었습니다.',
      user: userWithoutPassword 
    };
  },

  /**
   * 로그인 (Read)
   * @param {string} userId - 아이디
   * @param {string} password - 비밀번호
   * @returns {Object} { success: boolean, message: string, user?: Object }
   */
  login(userId, password) {
    const user = this.findByUserId(userId);

    if (!user) {
      return { success: false, message: '아이디 또는 비밀번호가 올바르지 않습니다.' };
    }

    if (user.password !== password) {
      return { success: false, message: '아이디 또는 비밀번호가 올바르지 않습니다.' };
    }

    // 현재 로그인한 사용자 정보 저장
    const { password: _, ...userWithoutPassword } = user;
    localStorage.setItem(CURRENT_USER_KEY, JSON.stringify(userWithoutPassword));

    return { 
      success: true, 
      message: '로그인 성공',
      user: userWithoutPassword 
    };
  },

  /**
   * 로그아웃
   */
  logout() {
    localStorage.removeItem(CURRENT_USER_KEY);
  },

  /**
   * 현재 로그인한 사용자 조회
   * @returns {Object|null} 사용자 객체 또는 null
   */
  getCurrentUser() {
    try {
      const raw = localStorage.getItem(CURRENT_USER_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch (error) {
      console.error('현재 사용자 조회 실패:', error);
      return null;
    }
  },

  /**
   * 로그인 상태 확인
   * @returns {boolean}
   */
  isLoggedIn() {
    return this.getCurrentUser() !== null;
  },

  /**
   * 회원정보 수정 (Update)
   * @param {string} userId - 수정할 회원 아이디
   * @param {Object} updateData - 수정할 데이터 { name?, email?, phone?, password? }
   * @returns {Object} { success: boolean, message: string, user?: Object }
   */
  update(userId, updateData) {
    const users = this.loadAll();
    const userIndex = users.findIndex(u => u.userId === userId);

    if (userIndex === -1) {
      return { success: false, message: '회원을 찾을 수 없습니다.' };
    }

    // 수정 가능한 필드만 업데이트
    const allowedFields = ['name', 'email', 'phone', 'password'];
    const updatedUser = { ...users[userIndex] };
    
    Object.keys(updateData).forEach(key => {
      if (allowedFields.includes(key) && updateData[key]) {
        updatedUser[key] = updateData[key];
      }
    });

    updatedUser.updatedAt = new Date().toISOString();
    users[userIndex] = updatedUser;
    this.saveAll(users);

    // 현재 로그인한 사용자 정보도 업데이트
    const currentUser = this.getCurrentUser();
    if (currentUser && currentUser.userId === userId) {
      const { password: _, ...userWithoutPassword } = updatedUser;
      localStorage.setItem(CURRENT_USER_KEY, JSON.stringify(userWithoutPassword));
    }

    const { password: _, ...userWithoutPassword } = updatedUser;
    return { 
      success: true, 
      message: '회원정보가 수정되었습니다.',
      user: userWithoutPassword 
    };
  },

  /**
   * 회원 탈퇴 (Delete)
   * @param {string} userId - 탈퇴할 회원 아이디
   * @param {string} password - 비밀번호 확인
   * @returns {Object} { success: boolean, message: string }
   */
  delete(userId, password) {
    const users = this.loadAll();
    const user = users.find(u => u.userId === userId);

    if (!user) {
      return { success: false, message: '회원을 찾을 수 없습니다.' };
    }

    if (user.password !== password) {
      return { success: false, message: '비밀번호가 일치하지 않습니다.' };
    }

    // 회원 삭제
    const filteredUsers = users.filter(u => u.userId !== userId);
    this.saveAll(filteredUsers);

    // 현재 로그인한 사용자면 로그아웃
    const currentUser = this.getCurrentUser();
    if (currentUser && currentUser.userId === userId) {
      this.logout();
    }

    return { 
      success: true, 
      message: '회원 탈퇴가 완료되었습니다.' 
    };
  },

  /**
   * 비밀번호 찾기 - 사용자 존재 확인
   * @param {string} userId - 아이디
   * @param {string} name - 이름
   * @param {string} phone - 전화번호
   * @returns {Object} { success: boolean, message: string, email?: string }
   */
  findPassword(userId, name, phone) {
    const user = this.findByUserId(userId);

    if (!user) {
      return { success: false, message: '입력하신 정보와 일치하는 회원을 찾을 수 없습니다.' };
    }

    if (user.name !== name || user.phone !== phone) {
      return { success: false, message: '입력하신 정보와 일치하는 회원을 찾을 수 없습니다.' };
    }

    // 실제 서비스에서는 임시 비밀번호 발급 또는 재설정 링크 전송
    return { 
      success: true, 
      message: '인증에 성공했습니다.',
      email: user.email 
    };
  },

  /**
   * 비밀번호 재설정
   * @param {string} userId - 아이디
   * @param {string} newPassword - 새 비밀번호
   * @returns {Object} { success: boolean, message: string }
   */
  resetPassword(userId, newPassword) {
    return this.update(userId, { password: newPassword });
  }
};

// 전역 객체로 export
window.UserStore = UserStore;
