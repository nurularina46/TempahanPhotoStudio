const axios = require('axios');

class SupabaseConfig {
  constructor() {
    this.url = process.env.SUPABASE_URL;
    this.key = process.env.SUPABASE_KEY;
    this.serviceKey = process.env.SUPABASE_SERVICE_KEY;
    
    if (!this.url || !this.key) {
      throw new Error('Supabase configuration is missing. Please check your .env file.');
    }
  }

  getHeaders(useServiceKey = false) {
    return {
      'Content-Type': 'application/json',
      'apikey': useServiceKey ? this.serviceKey : this.key,
      'Authorization': `Bearer ${useServiceKey ? this.serviceKey : this.key}`,
      'Prefer': 'return=representation'
    };
  }

  getUrl(endpoint) {
    return `${this.url}/rest/v1/${endpoint}`;
  }

  async request(method, endpoint, data = null, useServiceKey = false) {
    try {
      const config = {
        method,
        url: this.getUrl(endpoint),
        headers: this.getHeaders(useServiceKey)
      };

      if (data) {
        config.data = data;
      }

      const response = await axios(config);
      return {
        success: true,
        data: response.data
      };
    } catch (error) {
      console.error('Supabase request error:', error.response?.data || error.message);
      return {
        success: false,
        error: error.response?.data || error.message
      };
    }
  }
}

module.exports = new SupabaseConfig();

