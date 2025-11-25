class Validators {
  /**
   * Validate email format
   * @param {String} email
   * @returns {Boolean}
   */
  static isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  /**
   * Validate phone number (Malaysian format)
   * @param {String} phone
   * @returns {Boolean}
   */
  static isValidPhone(phone) {
    // Remove spaces and dashes
    const cleaned = phone.replace(/[\s-]/g, '');
    // Malaysian phone: 10-11 digits, may start with +60, 60, or 0
    const phoneRegex = /^(\+?60|0)?[1-9]\d{8,9}$/;
    return phoneRegex.test(cleaned);
  }

  /**
   * Validate payment creation request
   * @param {Object} data
   * @returns {Object} { valid: Boolean, errors: Array }
   */
  static validatePaymentRequest(data) {
    const errors = [];

    if (!data.name || typeof data.name !== 'string' || data.name.trim().length === 0) {
      errors.push('Name is required');
    }

    if (!data.email || typeof data.email !== 'string' || !this.isValidEmail(data.email)) {
      errors.push('Valid email is required');
    }

    if (!data.phone || typeof data.phone !== 'string' || !this.isValidPhone(data.phone)) {
      errors.push('Valid phone number is required (Malaysian format)');
    }

    if (!data.amount || isNaN(data.amount) || parseFloat(data.amount) <= 0) {
      errors.push('Valid amount greater than 0 is required');
    }

    if (parseFloat(data.amount) > 100000) {
      errors.push('Amount cannot exceed RM 100,000');
    }

    return {
      valid: errors.length === 0,
      errors
    };
  }
}

module.exports = Validators;

