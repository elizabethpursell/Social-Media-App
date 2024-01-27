var describe: any;
var it: any;
var expect: any;
var beforeEach: any;
var jasmine: any;

describe("Logic Tests", function() {

    it("1 + 1 = 2 ", function(){
        let one = 1;
        expect(one).toEqual(1);
        let two = one + one;
        expect(two).toEqual(2);
    });

    it("Adding 1 should work", function() {
        var foo = 0;
        foo += 1;
        expect(foo).toEqual(1);
    });

    it("Subtracting 1 should work", function () {
        var foo = 0;
        foo -= 1;
        expect(foo).toEqual(-1);
    });

});

describe("UI Tests", function () {

    // Decoding Google response
    it('Decode test JWT response', function() {
        // Create a mock JWT response
        const mockJwt = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InRlc3RAdGVzdC5jb20ifQ';
      
        // Call decodeJwtResponse with the mock JWT
        const decoded = decodeJwtResponse(mockJwt);
      
        // Expect the decoded object to match the expected content
        expect(decoded.email).toBe('test@test.com');
    });

    // Login Menu
    it("Posts are hidden if not authenticated", function() {
        expect( (<HTMLElement>document.getElementById("loginItems")).style.display ).toEqual("block");
        expect( (<HTMLElement>document.getElementById("authenticatedItems")).style.display ).toEqual("none");
    });

    it("Google login in button shown", function () {
        expect( (<HTMLElement>document.getElementById("g_id_onload"))).toBeDefined();
        expect( (<HTMLElement>document.getElementById("loginItems")).style.display ).toEqual("block");
    });

    it("Login in form shown", function() {
        expect( (<HTMLElement>document.getElementById("login"))).toBeDefined();
        expect( (<HTMLElement>document.getElementById("loginItems")).style.display ).toEqual("block");
    });

    it("Back button is hidden", function() {
        expect( (<HTMLElement>document.getElementById("backButton"))).toBeDefined();
        expect( (<HTMLElement>document.getElementById("authenticatedItems")).style.display).toEqual("none");
    });

    it("Profile button is hidden", function() {
        expect( (<HTMLElement>document.getElementById("profileButton"))).toBeDefined();
        expect( (<HTMLElement>document.getElementById("authenticatedItems")).style.display).toEqual("none");
    });

    it("Add new post button is hidden", function() {
        expect( (<HTMLElement>document.getElementById("showFormButton"))).toBeDefined();
        expect( (<HTMLElement>document.getElementById("authenticatedItems")).style.display).toEqual("none");
    });
});

function decodeJwtResponse(data: any) {
    var tokens = data.split(".");
    return JSON.parse(atob(tokens[1]));
}